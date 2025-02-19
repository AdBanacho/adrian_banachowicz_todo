package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.Category;
import ch.cern.todo.category.dataModels.CategoryResource;
import ch.cern.todo.category.dataModels.CategoryStatus;
import ch.cern.todo.exceptions.ValidationException;
import ch.cern.todo.searchEngine.CategorySearchEngineService;
import ch.cern.todo.searchEngine.SearchCriteria;
import ch.cern.todo.tasks.TaskService;
import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import ch.cern.todo.validation.InputFieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private static final String CATEGORY = "Category";

    private final CategoryRepository categoryRepository;
    private final TaskService taskService;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, TaskService taskService) {
        this.categoryRepository = categoryRepository;
        this.taskService = taskService;
    }

    @Override
    public Page<CategoryResource> getAllCategories(Pageable pageable, List<SearchCriteria> searchCriteriaList) {
        logger.debug("Entering getAllCategories with pageable: {} and searchCriteria: {}", pageable, searchCriteriaList);

        Specification<Category> baseSpec = (root, query, builder) -> builder.and(
                builder.notEqual(root.get("status"), CategoryStatus.DELETED),
                builder.equal(root.get("processedTo"), Timestamp.valueOf("9999-12-31 12:00:00"))
        );

        Specification<Category> dynamicSpec = Specification.where(null);
        for (SearchCriteria criteria : searchCriteriaList) {
            dynamicSpec = dynamicSpec.and(new CategorySearchEngineService(criteria));
        }

        Page<Category> categories = categoryRepository.findAll(baseSpec.and(dynamicSpec), pageable);
        logger.info("Retrieved {} categories", categories.getTotalElements());

        return categories.map(Category::transferToResource);
    }

    @Override
    public CategoryResource getCategory(String categoryName) {
        logger.debug("Retrieving category with name: {}", categoryName);
        Category category = categoryRepository.findByCategoryNameAndProcessedTo(categoryName).orElse(null);
        if (category == null) {
            logger.warn("Category with name '{}' not found", categoryName);
            return null;
        }
        logger.debug("Category found. Retrieving tasks for category '{}'", categoryName);
        List<TaskResource> tasksWithDetails = category.getTasks().stream()
                .map(taskService::mapToResourceWithFullNames)
                .toList();
        logger.debug("Mapped {} tasks for category '{}'", tasksWithDetails.size(), categoryName);
        return category.transferToResource(tasksWithDetails);
    }

    @Override
    public CategoryResource saveCategory(CategoryResource categoryResource) {
        logger.debug("Saving new category with name: {}", categoryResource.name());
        validateNewCategoryInput(categoryResource);
        Category categoryToSave = categoryResource.transferToNewEntity();
        Category savedCategory = categoryRepository.save(categoryToSave);
        logger.info("Category saved with ID: {}", savedCategory.getId());
        return savedCategory.transferToResource();
    }

    private void validateNewCategoryInput(CategoryResource categoryResource) {
        logger.debug("Validating new category input: {}", categoryResource);
        List<String> errorMessages = new ArrayList<>();
        String existingCategoryName = categoryRepository.findByCategoryNameAndProcessedTo(categoryResource.name())
                .map(Category::getName)
                .orElse(null);

        InputFieldValidator.validateIfEntityExists(CATEGORY, "name", existingCategoryName);
        InputFieldValidator.validateFieldNotEmpty(CATEGORY, "name", categoryResource.name(), errorMessages);

        if (!errorMessages.isEmpty()) {
            logger.error("Validation errors for new category: {}", errorMessages);
            throw new ValidationException(String.join(", \n", errorMessages));
        }
    }

    @Override
    public CategoryResource updateDetails(CategoryResource categoryResource) {
        logger.debug("Updating details for category with ID: {}", categoryResource.id());
        Category existingCategory = categoryRepository.findByIdAndProcessedTo(categoryResource.id()).orElse(null);
        validateUpdatingCategoryInput(categoryResource, existingCategory);
        Category categoryToUpdate = categoryResource.transferToExistingEntity(existingCategory);
        Category updatedCategory = saveUpdatedCategory(existingCategory, categoryToUpdate);
        logger.info("Category with ID: {} updated successfully", categoryResource.id());
        return updatedCategory.transferToResource();
    }

    /**
     * Validates the input for updating a category.
     *
     * @param categoryResource       the category resource with updated data
     * @param existingCategorySameId the existing category retrieved by ID
     */
    private void validateUpdatingCategoryInput(CategoryResource categoryResource, Category existingCategorySameId) {
        logger.debug("Validating update for category with ID: {}", categoryResource.id());
        List<String> errorMessages = new ArrayList<>();
        Category existingCategorySameName = categoryRepository.findByCategoryNameAndProcessedTo(categoryResource.name()).orElse(null);
        InputFieldValidator.validateIfNotEntityExists(CATEGORY, categoryResource.id(), existingCategorySameId);
        InputFieldValidator.validateIfCategoryNameIsUnique(existingCategorySameId, existingCategorySameName, errorMessages);
        InputFieldValidator.validateFieldNotEmpty(CATEGORY, "name", categoryResource.name(), errorMessages);
        if (!errorMessages.isEmpty()) {
            logger.error("Validation errors during update: {}", errorMessages);
            throw new ValidationException(String.join(", \n", errorMessages));
        }
    }

    @Override
    public void deleteCategory(String id) {
        logger.debug("Deleting category with ID: {}", id);
        Category existingCategory = categoryRepository.findByIdAndProcessedTo(id).orElse(null);
        InputFieldValidator.validateIfNotEntityExists(CATEGORY, id, existingCategory);
        Category categoryWithUpdatedStatus = existingCategory.updateStatus(CategoryStatus.DELETED);
        // Delete all tasks associated with this category.
        existingCategory.getTasks().stream()
                .map(Task::getId)
                .forEach(taskId -> {
                    logger.debug("Deleting task with ID: {} associated with category ID: {}", taskId, id);
                    taskService.deleteTask(taskId);
                });
        saveUpdatedCategory(existingCategory, categoryWithUpdatedStatus);
        logger.info("Category with ID: {} deleted successfully", id);
    }

    private Category saveUpdatedCategory(Category existingCategory, Category categoryWithUpdatedData) {
        logger.debug("Closing category entity for ID: {}", existingCategory.getId());
        existingCategory.closeCategoryEntity();
        categoryRepository.save(existingCategory);
        logger.debug("Saving updated category data for ID: {}", existingCategory.getId());
        return categoryRepository.save(categoryWithUpdatedData);
    }
}

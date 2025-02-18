package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.Category;
import ch.cern.todo.category.dataModels.CategoryResource;
import ch.cern.todo.exceptions.ValidationException;
import ch.cern.todo.tasks.TaskService;
import ch.cern.todo.tasks.dataModels.TaskResource;
import ch.cern.todo.validation.InputFieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY = "Category";
    private final CategoryRepository categoryRepository;
    private final TaskService taskService;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, TaskService taskService) {
        this.categoryRepository = categoryRepository;
        this.taskService = taskService;
    }

    @Override
    public List<CategoryResource> getAllCategories() {
        List<Category> categories = categoryRepository.findAllByProcessedTo();
        return categories.stream().map(Category::transferToResource).toList();
    }

    @Override
    public CategoryResource getCategory(String categoryName) {
        Category category = categoryRepository.findByCategoryNameAndProcessedTo(categoryName).orElse(null);
        if (category == null){
            return null;
        }
        List<TaskResource> tasksWithDetails = category.getTasks().stream().map(taskService::mapToResourceWithFullNames).toList();
        return category.transferToResource(tasksWithDetails);
    }

    @Override
    public CategoryResource saveCategory(CategoryResource categoryResource) {
        validateNewCategoryInput(categoryResource);
        Category categoryToSave = categoryResource.transferToNewEntity();
        Category savedCategory = categoryRepository.save(categoryToSave);
        return savedCategory.transferToResource();
    }

    private void validateNewCategoryInput(CategoryResource categoryResource) {
        List<String> errorMessages = new ArrayList<>();
        String existingCategoryName = categoryRepository.findByCategoryNameAndProcessedTo(categoryResource.name())
                .map(Category::getName)
                .orElse(null);

        InputFieldValidator.validateIfEntityExists(CATEGORY, "name", existingCategoryName);
        InputFieldValidator.validateFieldNotEmpty(CATEGORY,"name", categoryResource.name(), errorMessages);

        if (!errorMessages.isEmpty()) {
            throw new ValidationException(String.join(", \n", errorMessages));
        }
    }

    @Override
    public CategoryResource updateDetails(CategoryResource categoryResource) {
        Category existingCategory = categoryRepository.findByIdAndProcessedTo(categoryResource.id()).orElse(null);
        validateUpdatingCategoryInput(categoryResource, existingCategory);
        Category categoryToUpdate = categoryResource.transferToExistingEntity(existingCategory);
        existingCategory.closeCategoryEntity();
        categoryRepository.save(existingCategory);
        Category updatedCategory = categoryRepository.save(categoryToUpdate);
        return updatedCategory.transferToResource();
    }

    private static void validateUpdatingCategoryInput(CategoryResource categoryResource, Category category) {
        List<String> errorMessages = new ArrayList<>();
        InputFieldValidator.validateIfNotEntityExists(CATEGORY, categoryResource.id(), category);
        InputFieldValidator.validateFieldNotEmpty(CATEGORY, "name", categoryResource.name(), errorMessages);
        if (!errorMessages.isEmpty()) {
            throw new ValidationException(String.join(", \n", errorMessages));
        }
    }
}

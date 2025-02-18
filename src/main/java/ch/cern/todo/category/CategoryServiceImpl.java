package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.Category;
import ch.cern.todo.category.dataModels.CategoryResource;
import ch.cern.todo.tasks.TaskService;
import ch.cern.todo.tasks.dataModels.TaskResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {

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
}

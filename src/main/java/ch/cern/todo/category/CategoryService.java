package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.CategoryResource;

import java.util.List;

public interface CategoryService {

    List<CategoryResource> getAllCategories();

    CategoryResource getCategory(String categoryName);

    CategoryResource saveCategory(CategoryResource categoryResource);

    CategoryResource updateDetails(CategoryResource categoryResource);

    void deleteCategory(String id);
}

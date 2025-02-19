package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.CategoryResource;
import ch.cern.todo.searchEngine.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    Page<CategoryResource> getAllCategories(Pageable pageable, List<SearchCriteria> searchCriteriaList);

    CategoryResource getCategory(String categoryName);

    CategoryResource saveCategory(CategoryResource categoryResource);

    CategoryResource updateDetails(CategoryResource categoryResource);

    void deleteCategory(String id);
}

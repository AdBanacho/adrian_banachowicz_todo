package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.CategoryResource;
import ch.cern.todo.searchEngine.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing categories.
 * <p>
 * This interface provides operations to retrieve, create, update, and delete category resources.
 * </p>
 */
public interface CategoryService {

    /**
     * Retrieves a paginated list of categories based on the provided search criteria.
     *
     * @param pageable           the pagination and sorting information
     * @param searchCriteriaList a list of search criteria to filter the categories
     * @return a {@link org.springframework.data.domain.Page} of {@link CategoryResource} objects
     */
    Page<CategoryResource> getAllCategories(Pageable pageable, List<SearchCriteria> searchCriteriaList);

    /**
     * Retrieves a single category by its name.
     *
     * @param categoryName the name of the category to retrieve
     * @return a {@link CategoryResource} representing the category, or {@code null} if no category is found
     */
    CategoryResource getCategory(String categoryName);

    /**
     * Saves a new category.
     *
     * @param categoryResource the category resource containing the details of the category to be created
     * @return a {@link CategoryResource} representing the newly created category with its generated ID and persisted details
     */
    CategoryResource saveCategory(CategoryResource categoryResource);

    /**
     * Updates the details of an existing category.
     *
     * @param categoryResource the category resource containing updated details; the resource must have a valid ID
     * @return a {@link CategoryResource} representing the updated category
     */
    CategoryResource updateDetails(CategoryResource categoryResource);

    /**
     * Deletes the category identified by the specified ID.
     *
     * @param id the unique identifier of the category to be deleted
     */
    void deleteCategory(String id);
}

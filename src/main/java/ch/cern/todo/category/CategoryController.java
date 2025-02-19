package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.CategoryResource;
import ch.cern.todo.searchEngine.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves a paginated list of categories based on search criteria.
     *
     * <p>This endpoint is accessible to users with roles "USER" or "ADMIN".</p>
     *
     * @param page               the page number to retrieve (default is 0).
     * @param size               the number of items per page (default is 10).
     * @param sortBy             the field to sort by (default is "id").
     * @param ascending          whether to sort in ascending order (default is true).
     * @param searchCriteriaList the list of search criteria to filter categories.
     * @return a ResponseEntity containing a page of CategoryResource objects.
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<CategoryResource>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            @RequestBody List<SearchCriteria> searchCriteriaList) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(categoryService.getAllCategories(pageable, searchCriteriaList));
    }

    /**
     * Retrieves a single category by its name.
     *
     * <p>This endpoint is accessible to users with roles "USER" or "ADMIN".</p>
     *
     * @param categoryName the name of the category to retrieve.
     * @return ResponseEntity containing the corresponding CategoryResource.
     */
    @GetMapping(value = "/{categoryName}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CategoryResource> getCategory(@PathVariable String categoryName) {
        return ResponseEntity.ok(categoryService.getCategory(categoryName));
    }

    /**
     * Creates a new category.
     *
     * <p>This endpoint is restricted to users with the "ADMIN" role.</p>
     *
     * @param categoryResource the category resource containing the details for the new category.
     * @return a ResponseEntity containing the saved CategoryResource.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResource> saveCategory(@RequestBody CategoryResource categoryResource) {
        return ResponseEntity.ok(categoryService.saveCategory(categoryResource));
    }

    /**
     * Updates the details of an existing category.
     *
     * <p>This endpoint is restricted to users with the "ADMIN" role.</p>
     *
     * @param categoryResource the updated category resource.
     * @return a ResponseEntity containing the updated CategoryResource.
     */
    @PutMapping(value = "/updateDetails")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResource> updateDetails(@RequestBody CategoryResource categoryResource) {
        return ResponseEntity.ok(categoryService.updateDetails(categoryResource));
    }

    /**
     * Deletes an existing category by its ID.
     *
     * <p>This endpoint is restricted to users with the "ADMIN" role.</p>
     *
     * @param id the ID of the category to delete.
     * @return a ResponseEntity with an OK status if deletion is successful.
     */
    @PutMapping(value = "/deleteCategory/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}

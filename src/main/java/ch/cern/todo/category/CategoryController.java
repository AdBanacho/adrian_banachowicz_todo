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

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<CategoryResource>> getAllTasks(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "id") String sortBy,
                                                          @RequestParam(defaultValue = "true") boolean ascending,
                                                          @RequestBody List<SearchCriteria> searchCriteriaList){
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(categoryService.getAllCategories(pageable, searchCriteriaList));
    }

    @GetMapping(value = "/{categoryName}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CategoryResource> getCategory(@PathVariable String categoryName){
        return ResponseEntity.ok(categoryService.getCategory(categoryName));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResource> saveCategory(@RequestBody CategoryResource categoryResource){
        return ResponseEntity.ok(categoryService.saveCategory(categoryResource));
    }

    @PutMapping(value = "/updateDetails")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResource> updateDetails(@RequestBody CategoryResource categoryResource){
        return ResponseEntity.ok(categoryService.updateDetails(categoryResource));
    }

    @PutMapping(value = "/deleteCategory/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

}

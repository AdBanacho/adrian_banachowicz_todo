package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.CategoryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<CategoryResource>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping(value = "/{categoryName}")
    public ResponseEntity<CategoryResource> getCategory(@PathVariable String categoryName){
        return ResponseEntity.ok(categoryService.getCategory(categoryName));
    }

    @PostMapping
    public ResponseEntity<CategoryResource> saveCategory(@RequestBody CategoryResource categoryResource){
        return ResponseEntity.ok(categoryService.saveCategory(categoryResource));
    }

    @PutMapping(value = "/updateDetails")
    public ResponseEntity<CategoryResource> updateDetails(@RequestBody CategoryResource categoryResource){
        return ResponseEntity.ok(categoryService.updateDetails(categoryResource));
    }

    @PutMapping(value = "/deleteCategory/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

}

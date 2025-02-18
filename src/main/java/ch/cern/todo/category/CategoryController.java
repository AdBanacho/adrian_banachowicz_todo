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

    @GetMapping(value = "")
    public ResponseEntity<List<CategoryResource>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping(value = "/{categoryName}")
    public ResponseEntity<CategoryResource> getCategory(@PathVariable String categoryName){
        return ResponseEntity.ok(categoryService.getCategory(categoryName));
    }
}

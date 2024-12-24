package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.CategoryNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;


    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        logger.info("Fetched {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCategoryById(@PathVariable("id") int id) {
        try {
            Category category = categoryService.getCategoryById(id);
            logger.info("Fetched category with id: {}", id);
            return ResponseEntity.ok(category);
        } catch (CategoryNotFoundException e) {
            logger.error("Category not found with id: {}", id);
            return ResponseEntity.status(404).body("Category not found");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody Category category) {
        try {
            categoryService.createCategory(category.getName(), category.getDescription());
            logger.info("Created category: {}", category.getName());
            return ResponseEntity.status(201).body("Category created");
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error creating category: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable("id") int id, @RequestBody Category category) {
        try {
            categoryService.updateCategoryById(id, category.getName(), category.getDescription());
            logger.info("Updated category with id: {}", id);
            return ResponseEntity.ok("Category updated");
        } catch (CategoryNotFoundException e) {
            logger.error("Category not found with id: {}", id);
            return ResponseEntity.status(404).body("Category not found");
        } catch (Exception e) {
            logger.error("Error updating category: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error updating category: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable("id") int id) {
        try {
            categoryService.deleteCategoryById(id);
            logger.info("Deleted category with id: {}", id);
            return ResponseEntity.ok("Category deleted");
        } catch (CategoryNotFoundException e) {
            logger.error("Category not found with id: {}", id);
            return ResponseEntity.status(404).body("Category not found");
        }
    }
}

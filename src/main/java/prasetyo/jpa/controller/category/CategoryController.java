package prasetyo.jpa.controller.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import prasetyo.jpa.entity.Category;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.helper.ResponseHelper;
import prasetyo.jpa.request.category.CreateCategoryRequest;
import prasetyo.jpa.request.category.UpdateCategoryRequest;
import prasetyo.jpa.service.category.CategoryService;
import prasetyo.jpa.middleware.UseMiddleware;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ResponseHelper responseHelper;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @PostMapping
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<?> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        try {
            User user = (User) httpServletRequest.getAttribute("currentUser");
            if (user == null) {
                return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            Category category = new Category();
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            category.setColor(request.getColor());
            Category created = categoryService.createCategory(category, user);
            
            return responseHelper.success("Category created successfully", created);
        } catch (Exception e) {
            return responseHelper.error("Error creating category: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        try {
            User user = (User) httpServletRequest.getAttribute("currentUser");
            if (user == null) {
                return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            Category category = new Category();
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            category.setColor(request.getColor());
            category.setArchived(request.isArchived());
            Category updated = categoryService.updateCategory(id, category, user);
            
            return responseHelper.success("Category updated successfully", updated);
        } catch (IllegalArgumentException e) {
            return responseHelper.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return responseHelper.error("Error updating category: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            User user = (User) httpServletRequest.getAttribute("currentUser");
            if (user == null) {
                return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            categoryService.deleteCategory(id, user);
            return responseHelper.success("Category deleted successfully");
        } catch (IllegalArgumentException e) {
            return responseHelper.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return responseHelper.error("Error deleting category: " + e.getMessage());
        }
    }

    @GetMapping
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<?> getAllCategories(@RequestParam(required = false) boolean includeArchived) {
        try {
            User user = (User) httpServletRequest.getAttribute("currentUser");
            if (user == null) {
                return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            List<Category> categories = includeArchived ? 
                categoryService.getAllCategories(user) : 
                categoryService.getActiveCategories(user);
            
            return responseHelper.success("Categories retrieved successfully", categories);
        } catch (Exception e) {
            return responseHelper.error("Error fetching categories: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            User user = (User) httpServletRequest.getAttribute("currentUser");
            if (user == null) {
                return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            Category category = categoryService.getCategoryById(id, user);
            return responseHelper.success("Category retrieved successfully", category);
        } catch (IllegalArgumentException e) {
            return responseHelper.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return responseHelper.error("Error fetching category: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/archive")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<?> archiveCategory(@PathVariable Long id) {
        try {
            User user = (User) httpServletRequest.getAttribute("currentUser");
            if (user == null) {
                return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            Category category = categoryService.archiveCategory(id, user);
            return responseHelper.success("Category archived successfully", category);
        } catch (IllegalArgumentException e) {
            return responseHelper.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return responseHelper.error("Error archiving category: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/unarchive")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<?> unarchiveCategory(@PathVariable Long id) {
        try {
            User user = (User) httpServletRequest.getAttribute("currentUser");
            if (user == null) {
                return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            Category category = categoryService.unarchiveCategory(id, user);
            return responseHelper.success("Category unarchived successfully", category);
        } catch (IllegalArgumentException e) {
            return responseHelper.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return responseHelper.error("Error unarchiving category: " + e.getMessage());
        }
    }
}

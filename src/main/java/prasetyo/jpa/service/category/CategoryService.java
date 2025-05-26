package prasetyo.jpa.service.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import prasetyo.jpa.entity.Category;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.repository.CategoryRepository;
import prasetyo.jpa.repository.RegularTaskRepository;
import prasetyo.jpa.repository.RecurringTaskRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RegularTaskRepository regularTaskRepository;

    @Autowired
    private RecurringTaskRepository recurringTaskRepository;

    @Transactional
    public Category createCategory(Category category, User user) {
        category.setUser(user);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category updatedCategory, User user) {
        Category existingCategory = categoryRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new IllegalArgumentException("Category not found or unauthorized"));
        
        existingCategory.setName(updatedCategory.getName());
        existingCategory.setDescription(updatedCategory.getDescription());
        existingCategory.setColor(updatedCategory.getColor());
        existingCategory.setArchived(updatedCategory.isArchived());
        
        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(Long id, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new IllegalArgumentException("Category not found or unauthorized"));
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories(User user) {
        return categoryRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Category> getActiveCategories(User user) {
        return categoryRepository.findByUserAndIsArchivedFalse(user);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id, User user) {
        return categoryRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new IllegalArgumentException("Category not found or unauthorized"));
    }

    @Transactional
    public Category archiveCategory(Long id, User user) {
        Category category = getCategoryById(id, user);
        category.archive();
        return categoryRepository.save(category);
    }

    @Transactional
    public Category unarchiveCategory(Long id, User user) {
        Category category = getCategoryById(id, user);
        category.unarchive();
        return categoryRepository.save(category);
    }
}
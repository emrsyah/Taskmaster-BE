package prasetyo.jpa.service.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import prasetyo.jpa.entity.Category;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.helper.TransactionHelper;
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

    @Autowired
    private TransactionHelper transactionHelper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Category createCategory(Category category, User user) {
        return transactionHelper.executeWithRollback(() -> {
            category.setUser(user);
            return categoryRepository.save(category);
        }, "createCategory");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Category updateCategory(Long id, Category updatedCategory, User user) {
        return transactionHelper.executeWithRollback(() -> {
            Category existingCategory = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or unauthorized"));
            
            existingCategory.setName(updatedCategory.getName());
            existingCategory.setDescription(updatedCategory.getDescription());
            existingCategory.setColor(updatedCategory.getColor());
            existingCategory.setArchived(updatedCategory.isArchived());
            
            return categoryRepository.save(existingCategory);
        }, "updateCategory");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteCategory(Long id, User user) {
        transactionHelper.executeVoidWithRollback(() -> {
            Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or unauthorized"));
            categoryRepository.delete(category);
        }, "deleteCategory");
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<Category> getAllCategories(User user) {
        return categoryRepository.findByUser(user);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<Category> getActiveCategories(User user) {
        return categoryRepository.findByUserAndIsArchivedFalse(user);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Category getCategoryById(Long id, User user) {
        return categoryRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new IllegalArgumentException("Category not found or unauthorized"));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Category archiveCategory(Long id, User user) {
        Category category = getCategoryById(id, user);
        category.archive();
        return categoryRepository.save(category);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Category unarchiveCategory(Long id, User user) {
        Category category = getCategoryById(id, user);
        category.unarchive();
        return categoryRepository.save(category);
    }
}
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    @Transactional(propagation = Propagation.REQUIRED)
    public Category createCategory(Category category, User user) {
        return transactionHelper.executeWithRollback(() -> {
            try {
                category.setUser(user);
                return categoryRepository.save(category);
            } catch (Exception e) {
                log.error("Error creating category: {}", e.getMessage());
                throw e;
            }
        }, "createCategory");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Category updateCategory(Long id, Category updatedCategory, User user) {
        return transactionHelper.executeWithRollback(() -> {
            try {
                Category existingCategory = categoryRepository.findByIdAndUser(id, user)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found or unauthorized"));
                
                existingCategory.setName(updatedCategory.getName());
                existingCategory.setDescription(updatedCategory.getDescription());
                existingCategory.setColor(updatedCategory.getColor());
                existingCategory.setArchived(updatedCategory.isArchived());
                
                return categoryRepository.save(existingCategory);
            } catch (Exception e) {
                log.error("Error updating category: {}", e.getMessage());
                throw e;
            }
        }, "updateCategory");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCategory(Long id, User user) {
        transactionHelper.executeVoidWithRollback(() -> {
            try {
                Category category = categoryRepository.findByIdAndUser(id, user)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found or unauthorized"));
                categoryRepository.delete(category);
            } catch (Exception e) {
                log.error("Error deleting category: {}", e.getMessage());
                throw e;
            }
        }, "deleteCategory");
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories(User user) {
        try {
            return categoryRepository.findByUser(user);
        } catch (Exception e) {
            log.error("Error fetching categories: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Category> getActiveCategories(User user) {
        try {
            return categoryRepository.findByUserAndIsArchivedFalse(user);
        } catch (Exception e) {
            log.error("Error fetching active categories: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id, User user) {
        try {
            return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or unauthorized"));
        } catch (Exception e) {
            log.error("Error fetching category by id: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Category archiveCategory(Long id, User user) {
        return transactionHelper.executeWithRollback(() -> {
            try {
                Category category = getCategoryById(id, user);
                category.archive();
                return categoryRepository.save(category);
            } catch (Exception e) {
                log.error("Error archiving category: {}", e.getMessage());
                throw e;
            }
        }, "archiveCategory");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Category unarchiveCategory(Long id, User user) {
        return transactionHelper.executeWithRollback(() -> {
            try {
                Category category = getCategoryById(id, user);
                category.unarchive();
                return categoryRepository.save(category);
            } catch (Exception e) {
                log.error("Error unarchiving category: {}", e.getMessage());
                throw e;
            }
        }, "unarchiveCategory");
    }
}
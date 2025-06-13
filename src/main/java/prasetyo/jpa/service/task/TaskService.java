package prasetyo.jpa.service.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import prasetyo.jpa.entity.AbstractTask;
import prasetyo.jpa.entity.Category;
import prasetyo.jpa.entity.RecurringTask;
import prasetyo.jpa.entity.RegularTask;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.repository.RecurringTaskRepository;
import prasetyo.jpa.repository.RegularTaskRepository;
import prasetyo.jpa.request.task.CreateTaskRequest;
import prasetyo.jpa.request.task.UpdateTaskRequest;
import prasetyo.jpa.service.category.CategoryService;

@Service
public class TaskService {
    @Autowired
    private RegularTaskRepository regularTaskRepository;

    @Autowired
    private RecurringTaskRepository recurringTaskRepository;

    @Autowired
    private TaskSequenceService taskSequenceService;

    @Autowired
    private CategoryService categoryService;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public RegularTask getRegularTask(String uuid, User user) {
        try {
            RegularTask task = regularTaskRepository.findByUuid(uuid).orElse(null);
            if (task == null) {
                return null;
            }
            if (task.getUser() != null && task.getUser().getId().equals(user.getId())) {
                return task;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public RecurringTask getRecurringTask(String uuid, User user) {
        try {
            RecurringTask task = recurringTaskRepository.findByUuid(uuid).orElse(null);
            if (task == null) {
                return null;
            }
            if (task.getUser() != null && task.getUser().getId().equals(user.getId())) {
                return task;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<RegularTask> getRegularTasks(User user) {
        return regularTaskRepository.findByUser(user);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<RecurringTask> getRecurringTasks(User user) {
        return recurringTaskRepository.findByUser(user);
    }

    @Transactional
    public void deleteTask(String uuid, User user) {
        // Try regular task first
        RegularTask regularTask = getRegularTask(uuid, user);
        if (regularTask != null) {
            regularTaskRepository.delete(regularTask);
            return;
        }

        // Then try recurring task
        RecurringTask recurringTask = getRecurringTask(uuid, user);
        if (recurringTask != null) {
            recurringTaskRepository.delete(recurringTask);
            return;
        }

        throw new IllegalArgumentException("Task not found");
    }

    @Transactional
    @Deprecated(since = "1.0", forRemoval = true)
    public void deleteRegularTask(String uuid, User user) {
        RegularTask task = regularTaskRepository.findByUuid(uuid).orElse(null);
        if (task != null && task.getUser().getId().equals(user.getId())) {
            regularTaskRepository.delete(task);
        }
    }

    @Transactional
    @Deprecated(since = "1.0", forRemoval = true)
    public void deleteRecurringTask(String uuid, User user) {
        RecurringTask task = recurringTaskRepository.findByUuid(uuid).orElse(null);
        if (task != null && task.getUser().getId().equals(user.getId())) {
            recurringTaskRepository.delete(task);
        }
    }

    @Transactional
    public void archiveRegularTask(String uuid) {
        RegularTask task = regularTaskRepository.findByUuid(uuid).orElse(null);
        if (task != null) {
            task.archive();
            regularTaskRepository.save(task);
        }
    }

    @Transactional
    public void archiveRecurringTask(String uuid) {
        RecurringTask task = recurringTaskRepository.findByUuid(uuid).orElse(null);
        if (task != null) {
            task.archive();
            recurringTaskRepository.save(task);
        }
    }

    @Transactional
    public void unarchiveRegularTask(String uuid) {
        RegularTask task = regularTaskRepository.findByUuid(uuid).orElse(null);
        if (task != null) {
            task.unarchive();
            regularTaskRepository.save(task);
        }
    }

    @Transactional
    public void unarchiveRecurringTask(String uuid) {
        RecurringTask task = recurringTaskRepository.findByUuid(uuid).orElse(null);
        if (task != null) {
            task.unarchive();
            recurringTaskRepository.save(task);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Object updateTask(String uuid, UpdateTaskRequest request, User user) {
        // First check if it's a regular task
        RegularTask regularTask = getRegularTask(uuid, user);
        if (regularTask != null) {
            // If it's a regular task but request is for recurring, return null
            if (request.getTaskType() != CreateTaskRequest.TaskType.REGULAR) {
                throw new IllegalArgumentException("Cannot change task type from regular to recurring");
            }
            
            regularTask.setTitle(request.getTitle());
            regularTask.setDescription(request.getDescription());
            regularTask.setPriority(request.getPriority());
            regularTask.setDeadline(request.getDeadline());
            regularTask.setArchived(request.isArchived());
            regularTask.setCompleted(request.isCompleted());
            // Set category if provided
            if (request.getCategoryId() != null) {
                Category category = categoryService.getCategoryById(request.getCategoryId(), user);
                regularTask.setCategory(category);
            }
            return regularTaskRepository.save(regularTask);
        }

        // Then check if it's a recurring task
        RecurringTask recurringTask = getRecurringTask(uuid, user);
        if (recurringTask != null) {
            // If it's a recurring task but request is for regular, return null
            if (request.getTaskType() != CreateTaskRequest.TaskType.RECURRING) {
                throw new IllegalArgumentException("Cannot change task type from recurring to regular");
            }
            
            recurringTask.setTitle(request.getTitle());
            recurringTask.setDescription(request.getDescription());
            recurringTask.setPriority(request.getPriority());
            recurringTask.setRecurrenceDays(request.getRecurrenceDays());
            recurringTask.setDoneDates(request.getDoneDates());
            recurringTask.setArchived(request.isArchived());
            recurringTask.setCompleted(request.isCompleted());
            // Set category if provided
            if (request.getCategoryId() != null) {
                Category category = categoryService.getCategoryById(request.getCategoryId(), user);
                recurringTask.setCategory(category);
            }
            return recurringTaskRepository.save(recurringTask);
        }

        return null;
    }

    @Transactional
    public RegularTask createRegularTaskFromDto(CreateTaskRequest dto, User user) {
        if (!dto.isValid()) {
            throw new IllegalArgumentException("Invalid task data");
        }

        RegularTask task = new RegularTask();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setDeadline(dto.getDeadline());  // Can be null
        task.setUser(user);
        task.setCompleted(false);  // Default value
        task.setArchived(false);   // Default value
        task.setSequenceNumber(taskSequenceService.getNextSequenceNumber());
        
        // Add category if categoryId is provided
        if (dto.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(dto.getCategoryId(), user);
            task.setCategory(category);
        }

        return regularTaskRepository.save(task);
    }

    @Transactional
    public RecurringTask createRecurringTaskFromDto(CreateTaskRequest dto, User user) {
        if (!dto.isValid()) {
            throw new IllegalArgumentException("Invalid task data");
        }
        
        if (dto.getRecurrenceDays() == null || dto.getRecurrenceDays().isEmpty()) {
            throw new IllegalArgumentException("Recurrence days are required for recurring tasks");
        }

        RecurringTask task = new RecurringTask();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setRecurrenceDays(dto.getRecurrenceDays());
        task.setDoneDates(new ArrayList<>());  // Initialize with empty list
        task.setUser(user);
        task.setCompleted(false);  // Default value
        task.setArchived(false);   // Default value
        task.setSequenceNumber(taskSequenceService.getNextSequenceNumber());
        
        // Add category if categoryId is provided
        if (dto.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(dto.getCategoryId(), user);
            task.setCategory(category);
        }

        return recurringTaskRepository.save(task);
    }

    @Transactional
    public Object toggleTaskDone(String uuid, User user, boolean isDone) {
        // First check if it's a regular task
        RegularTask regularTask = getRegularTask(uuid, user);
        if (regularTask != null) {
            regularTask.setCompleted(isDone);
            return regularTaskRepository.save(regularTask);
        }

        // Then check if it's a recurring task
        RecurringTask recurringTask = getRecurringTask(uuid, user);
        if (recurringTask != null) {
            recurringTask.setCompleted(isDone);
            if (isDone) {
                if (recurringTask.getDoneDates() == null) {
                    recurringTask.setDoneDates(new ArrayList<>());
                }
                recurringTask.getDoneDates().add(new Date());
            }
            return recurringTaskRepository.save(recurringTask);
        }

        return null;
    }

    @Transactional
    public Object toggleTaskArchive(String uuid, User user, boolean isArchived) {
        // First check if it's a regular task
        RegularTask regularTask = getRegularTask(uuid, user);
        if (regularTask != null) {
            if (isArchived) {
                regularTask.archive();
            } else {
                regularTask.unarchive();
            }
            return regularTaskRepository.save(regularTask);
        }

        // Then check if it's a recurring task
        RecurringTask recurringTask = getRecurringTask(uuid, user);
        if (recurringTask != null) {
            if (isArchived) {
                recurringTask.archive();
            } else {
                recurringTask.unarchive();
            }
            return recurringTaskRepository.save(recurringTask);
        }

        return null;
    }

    @Transactional
    public RegularTask updateRegularTask(RegularTask task) {
        return regularTaskRepository.save(task);
    }

    @Transactional
    public RecurringTask updateRecurringTask(RecurringTask task) {
        return recurringTaskRepository.save(task);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Object archiveTask(String uuid, User user) {
        try {
            // Try regular task first
            RegularTask regularTask = getRegularTask(uuid, user);
            if (regularTask != null) {
                regularTask.archive();
                return regularTaskRepository.save(regularTask);
            }

            // Then try recurring task
            RecurringTask recurringTask = getRecurringTask(uuid, user);
            if (recurringTask != null) {
                recurringTask.archive();
                return recurringTaskRepository.save(recurringTask);
            }

            throw new IllegalArgumentException("Task not found");
        } catch (Exception e) {
            throw new IllegalArgumentException("Error archiving task: " + e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Object unarchiveTask(String uuid, User user) {
        try {
            // Try regular task first
            RegularTask regularTask = getRegularTask(uuid, user);
            if (regularTask != null) {
                regularTask.unarchive();
                return regularTaskRepository.save(regularTask);
            }

            // Then try recurring task
            RecurringTask recurringTask = getRecurringTask(uuid, user);
            if (recurringTask != null) {
                recurringTask.unarchive();
                return recurringTaskRepository.save(recurringTask);
            }

            throw new IllegalArgumentException("Task not found");
        } catch (Exception e) {
            throw new IllegalArgumentException("Error unarchiving task: " + e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Object undoTaskCompletion(String uuid, User user) {
        try {
            // Try regular task first
            RegularTask regularTask = getRegularTask(uuid, user);
            if (regularTask != null) {
                regularTask.setCompleted(false);
                regularTask.setStatus(AbstractTask.TaskStatus.IN_PROGRESS);
                return regularTaskRepository.save(regularTask);
            }

            // Then try recurring task
            RecurringTask recurringTask = getRecurringTask(uuid, user);
            if (recurringTask != null) {
                recurringTask.setCompleted(false);
                recurringTask.setStatus(AbstractTask.TaskStatus.IN_PROGRESS);
                // Remove the last done date if any
                if (recurringTask.getDoneDates() != null && !recurringTask.getDoneDates().isEmpty()) {
                    recurringTask.getDoneDates().remove(recurringTask.getDoneDates().size() - 1);
                }
                return recurringTaskRepository.save(recurringTask);
            }

            throw new IllegalArgumentException("Task not found");
        } catch (Exception e) {
            throw new IllegalArgumentException("Error undoing task completion: " + e.getMessage());
        }
    }
}
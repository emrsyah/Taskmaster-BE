package prasetyo.jpa.service.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import prasetyo.jpa.helper.TransactionHelper;
import prasetyo.jpa.repository.RecurringTaskRepository;
import prasetyo.jpa.repository.RegularTaskRepository;
import prasetyo.jpa.request.task.CreateTaskRequest;
import prasetyo.jpa.request.task.UpdateTaskRequest;
import prasetyo.jpa.service.category.CategoryService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    @Autowired
    private TransactionHelper transactionHelper;

    @Transactional(readOnly = true)
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
            log.error("Error fetching regular task: {}", e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
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
            log.error("Error fetching recurring task: {}", e.getMessage());
            return null;
        }
    }
    
    @Transactional(readOnly = true)
    public List<RegularTask> getRegularTasks(User user) {
        try {
            return regularTaskRepository.findByUser(user);
        } catch (Exception e) {
            log.error("Error fetching regular tasks: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<RecurringTask> getRecurringTasks(User user) {
        try {
            return recurringTaskRepository.findByUser(user);
        } catch (Exception e) {
            log.error("Error fetching recurring tasks: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteTask(String uuid, User user) {
        transactionHelper.executeVoidWithRollback(() -> {
            try {
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
            } catch (Exception e) {
                log.error("Error deleting task: {}", e.getMessage());
                throw e;
            }
        }, "deleteTask");
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
        return transactionHelper.executeWithRollback(() -> {
            try {
                // First check if it's a regular task
                RegularTask regularTask = getRegularTask(uuid, user);
                if (regularTask != null) {
                    if (request.getTaskType() != CreateTaskRequest.TaskType.REGULAR) {
                        throw new IllegalArgumentException("Cannot change task type from regular to recurring");
                    }
                    
                    regularTask.setTitle(request.getTitle());
                    regularTask.setDescription(request.getDescription());
                    regularTask.setPriority(request.getPriority());
                    regularTask.setDeadline(request.getDeadline());
                    regularTask.setArchived(request.isArchived());
                    regularTask.setCompleted(request.isCompleted());
                    
                    if (request.getCategoryId() != null) {
                        Category category = categoryService.getCategoryById(request.getCategoryId(), user);
                        regularTask.setCategory(category);
                    }
                    return regularTaskRepository.save(regularTask);
                }

                // Then check if it's a recurring task
                RecurringTask recurringTask = getRecurringTask(uuid, user);
                if (recurringTask != null) {
                    if (request.getTaskType() != CreateTaskRequest.TaskType.RECURRING) {
                        throw new IllegalArgumentException("Cannot change task type from recurring to regular");
                    }
                    
                    recurringTask.setTitle(request.getTitle());
                    recurringTask.setDescription(request.getDescription());
                    recurringTask.setPriority(request.getPriority());
                    recurringTask.setArchived(request.isArchived());
                    recurringTask.setCompleted(request.isCompleted());
                    
                    if (request.getCategoryId() != null) {
                        Category category = categoryService.getCategoryById(request.getCategoryId(), user);
                        recurringTask.setCategory(category);
                    }
                    
                    if (request.getRecurrenceDays() != null && !request.getRecurrenceDays().isEmpty()) {
                        recurringTask.setRecurrenceDays(new HashSet<>(request.getRecurrenceDays()));
                    }
                    
                    return recurringTaskRepository.save(recurringTask);
                }

                throw new IllegalArgumentException("Task not found");
            } catch (Exception e) {
                log.error("Error updating task: {}", e.getMessage());
                throw e;
            }
        }, "updateTask");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RegularTask createRegularTaskFromDto(CreateTaskRequest dto, User user) {
        return transactionHelper.executeWithRollback(() -> {
            try {
                if (!dto.isValid()) {
                    throw new IllegalArgumentException("Invalid task data");
                }

                RegularTask task = new RegularTask();
                task.setTitle(dto.getTitle());
                task.setDescription(dto.getDescription());
                task.setPriority(dto.getPriority());
                task.setDeadline(dto.getDeadline());
                task.setUser(user);
                task.setCompleted(false);
                task.setArchived(false);
                task.setSequenceNumber(taskSequenceService.getNextSequenceNumber());
                
                if (dto.getCategoryId() != null) {
                    Category category = categoryService.getCategoryById(dto.getCategoryId(), user);
                    task.setCategory(category);
                }

                return regularTaskRepository.save(task);
            } catch (Exception e) {
                log.error("Error creating regular task: {}", e.getMessage());
                throw e;
            }
        }, "createRegularTask");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RecurringTask createRecurringTaskFromDto(CreateTaskRequest dto, User user) {
        return transactionHelper.executeWithRollback(() -> {
            try {
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
                task.setRecurrenceDays(new HashSet<>(dto.getRecurrenceDays()));
                task.setDoneDates(new HashSet<>());
                task.setUser(user);
                task.setCompleted(false);
                task.setArchived(false);
                task.setSequenceNumber(taskSequenceService.getNextSequenceNumber());
                
                if (dto.getCategoryId() != null) {
                    Category category = categoryService.getCategoryById(dto.getCategoryId(), user);
                    task.setCategory(category);
                }

                return recurringTaskRepository.save(task);
            } catch (Exception e) {
                log.error("Error creating recurring task: {}", e.getMessage());
                throw e;
            }
        }, "createRecurringTask");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Object toggleTaskDone(String uuid, User user, boolean isDone) {
        return transactionHelper.executeWithRollback(() -> {
            try {
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
                            recurringTask.setDoneDates(new HashSet<>());
                        }
                        recurringTask.getDoneDates().add(new Date());
                    }
                    return recurringTaskRepository.save(recurringTask);
                }

                throw new IllegalArgumentException("Task not found");
            } catch (Exception e) {
                log.error("Error toggling task done status: {}", e.getMessage());
                throw e;
            }
        }, "toggleTaskDone");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Object toggleTaskArchive(String uuid, User user, boolean isArchived) {
        return transactionHelper.executeWithRollback(() -> {
            try {
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

                throw new IllegalArgumentException("Task not found");
            } catch (Exception e) {
                log.error("Error toggling task archive status: {}", e.getMessage());
                throw e;
            }
        }, "toggleTaskArchive");
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
        return transactionHelper.executeWithRollback(() -> {
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
                    if (recurringTask.getDoneDates() != null && !recurringTask.getDoneDates().isEmpty()) {
                        recurringTask.getDoneDates().remove(recurringTask.getDoneDates().size() - 1);
                    }
                    return recurringTaskRepository.save(recurringTask);
                }

                throw new IllegalArgumentException("Task not found");
            } catch (Exception e) {
                log.error("Error undoing task completion: {}", e.getMessage());
                throw e;
            }
        }, "undoTaskCompletion");
    }
}
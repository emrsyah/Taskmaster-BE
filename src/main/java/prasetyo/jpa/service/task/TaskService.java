package prasetyo.jpa.service.task;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import prasetyo.jpa.entity.RecurringTask;
import prasetyo.jpa.entity.RegularTask;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.repository.RecurringTaskRepository;
import prasetyo.jpa.repository.RegularTaskRepository;
import prasetyo.jpa.request.task.CreateTaskRequest;
import prasetyo.jpa.request.task.UpdateTaskRequest;

@Service
public class TaskService {
    @Autowired
    private RegularTaskRepository regularTaskRepository;

    @Autowired
    private RecurringTaskRepository recurringTaskRepository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public RegularTask getRegularTask(Long id, User user) {
        RegularTask task = regularTaskRepository.findById(id).orElse(null);
        if (task == null) {
            return null;
        }
        if (task.getUser() != null && task.getUser().getId().equals(user.getId())) {
            return task;
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public RecurringTask getRecurringTask(Long id, User user) {
        RecurringTask task = recurringTaskRepository.findById(id).orElse(null);
        if (task == null) {
            return null;
        }
        if (task.getUser() != null && task.getUser().getId().equals(user.getId())) {
            return task;
        }
        return null;
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
    public void deleteRegularTask(Long id, User user) {
        RegularTask task = regularTaskRepository.findById(id).orElse(null);
        if (task != null && task.getUser().getId().equals(user.getId())) {
            regularTaskRepository.deleteById(id);
        }
    }

    @Transactional
    public void deleteRecurringTask(Long id, User user) {
        RecurringTask task = recurringTaskRepository.findById(id).orElse(null);
        if (task != null && task.getUser().getId().equals(user.getId())) {
            recurringTaskRepository.deleteById(id);
        }
    }

    @Transactional
    public void archiveRegularTask(Long id) {
        RegularTask task = regularTaskRepository.findById(id).orElse(null);
        if (task != null) {
            task.archive();
            regularTaskRepository.save(task);
        }
    }

    @Transactional
    public void archiveRecurringTask(Long id) {
        RecurringTask task = recurringTaskRepository.findById(id).orElse(null);
        if (task != null) {
            task.archive();
            recurringTaskRepository.save(task);
        }
    }

    @Transactional
    public void unarchiveRegularTask(Long id) {
        RegularTask task = regularTaskRepository.findById(id).orElse(null);
        if (task != null) {
            task.unarchive();
            regularTaskRepository.save(task);
        }
    }

    @Transactional
    public void unarchiveRecurringTask(Long id) {
        RecurringTask task = recurringTaskRepository.findById(id).orElse(null);
        if (task != null) {
            task.unarchive();
            recurringTaskRepository.save(task);
        }
    }

    @Transactional
    public Object updateTask(Long id, UpdateTaskRequest request, User user) {
        // First check if it's a regular task
        RegularTask regularTask = getRegularTask(id, user);
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
            
            return regularTaskRepository.save(regularTask);
        }

        // Then check if it's a recurring task
        RecurringTask recurringTask = getRecurringTask(id, user);
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
            
            return recurringTaskRepository.save(recurringTask);
        }

        return null;
    }

    @Transactional
    public RegularTask createRegularTaskFromDto(prasetyo.jpa.request.task.CreateTaskRequest dto, User user) {
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
        return regularTaskRepository.save(task);
    }

    @Transactional
    public RecurringTask createRecurringTaskFromDto(prasetyo.jpa.request.task.CreateTaskRequest dto, User user) {
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
        return recurringTaskRepository.save(task);
    }
}
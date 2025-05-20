package prasetyo.jpa.service.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import prasetyo.jpa.entity.RecurringTask;
import prasetyo.jpa.entity.RegularTask;
import prasetyo.jpa.repository.RecurringTaskRepository;
import prasetyo.jpa.repository.RegularTaskRepository;

@Service
public class TaskService {
    @Autowired
    private RegularTaskRepository regularTaskRepository;

    @Autowired
    private RecurringTaskRepository recurringTaskRepository;

    public void deleteRegularTask(Long id) {
        regularTaskRepository.deleteById(id);
    }

    public void deleteRecurringTask(Long id) {
        recurringTaskRepository.deleteById(id);
    }

    public RegularTask getRegularTask(Long id) {
        return regularTaskRepository.findById(id).orElse(null);
    }

    public RecurringTask getRecurringTask(Long id) {
        return recurringTaskRepository.findById(id).orElse(null);
    }
    
    public List<RegularTask> getRegularTasks() {
        return regularTaskRepository.findAll();
    }

    public List<RecurringTask> getRecurringTasks() {
        return recurringTaskRepository.findAll();
    }

    public List<RegularTask> getRegularTasksByToken(String token) {
        return regularTaskRepository.findByToken(token).orElse(null);
    }

    public List<RecurringTask> getRecurringTasksByToken(String token) {
        return recurringTaskRepository.findByToken(token).orElse(null);
    }
    
    // public List<RegularTask> getRegularTasksByCategory(Long categoryId) {
    //     return regularTaskRepository.findByCategoryId(categoryId);
    // }

    // public List<RecurringTask> getRecurringTasksByCategory(Long categoryId) {
    //     return recurringTaskRepository.findByCategoryId(categoryId);
    // }
    
    public void archiveRegularTask(Long id) {
        RegularTask task = regularTaskRepository.findById(id).orElse(null);
        if (task != null) {
            task.archive();
            regularTaskRepository.save(task);
        }
    }

    public void archiveRecurringTask(Long id) {
        RecurringTask task = recurringTaskRepository.findById(id).orElse(null);
        if (task != null) {
            task.archive();
            recurringTaskRepository.save(task);
        }
    }

    public void unarchiveRegularTask(Long id) {
        RegularTask task = regularTaskRepository.findById(id).orElse(null);
        if (task != null) {
            task.unarchive();
            regularTaskRepository.save(task);
        }
    }

    public void unarchiveRecurringTask(Long id) {
        RecurringTask task = recurringTaskRepository.findById(id).orElse(null);
        if (task != null) {
            task.unarchive();
            recurringTaskRepository.save(task);
        }
    }

    public void updateRegularTask(RegularTask task) {
        regularTaskRepository.save(task);
    }

    public void updateRecurringTask(RecurringTask task) {
        recurringTaskRepository.save(task);
    }

    public RegularTask createRegularTaskFromDto(prasetyo.jpa.request.task.CreateTaskRequest dto) {
        RegularTask task = new RegularTask();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setDeadline(dto.getDeadline());
        // Set other fields as needed, e.g., category, createdAt, etc.
        return regularTaskRepository.save(task);
    }

    public RecurringTask createRecurringTaskFromDto(prasetyo.jpa.request.task.CreateTaskRequest dto) {
        RecurringTask task = new RecurringTask();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setRecurrenceDays(dto.getRecurrenceDays());
        // Set other fields as needed, e.g., category, createdAt, etc.
        return recurringTaskRepository.save(task);
    }
}
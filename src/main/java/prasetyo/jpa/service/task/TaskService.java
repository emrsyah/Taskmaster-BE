package prasetyo.jpa.service.task;

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
    public void updateRegularTask(RegularTask task) {
        regularTaskRepository.save(task);
    }

    @Transactional
    public void updateRecurringTask(RecurringTask task) {
        recurringTaskRepository.save(task);
    }

    @Transactional
    public RegularTask createRegularTaskFromDto(prasetyo.jpa.request.task.CreateTaskRequest dto, User user) {
        RegularTask task = new RegularTask();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setDeadline(dto.getDeadline());
        task.setUser(user);
        return regularTaskRepository.save(task);
    }

    @Transactional
    public RecurringTask createRecurringTaskFromDto(prasetyo.jpa.request.task.CreateTaskRequest dto, User user) {
        RecurringTask task = new RecurringTask();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setRecurrenceDays(dto.getRecurrenceDays());
        task.setUser(user);
        return recurringTaskRepository.save(task);
    }
}
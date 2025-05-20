package prasetyo.jpa.controller.task;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import prasetyo.jpa.entity.RecurringTask;
import prasetyo.jpa.entity.RegularTask;
import prasetyo.jpa.helper.ResponseHelper;
import prasetyo.jpa.request.task.CreateTaskRequest;
import prasetyo.jpa.service.task.TaskService;

@RestController
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private ResponseHelper responseHelper;

    @Autowired
    private Validator validator;

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskRequest request) {
        if (request.getTaskType() == CreateTaskRequest.TaskType.REGULAR) {
            RegularTask createdTask = taskService.createRegularTaskFromDto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } else if (request.getTaskType() == CreateTaskRequest.TaskType.RECURRING) {
            RecurringTask createdTask = taskService.createRecurringTaskFromDto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } else {
            return responseHelper.error("Invalid task type");
        }
    }

    @DeleteMapping("/tasks/regular/{id}")
    public ResponseEntity<Void> deleteRegularTask(@PathVariable Long id) {
        taskService.deleteRegularTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/regular")
    public ResponseEntity<List<RegularTask>> getAllRegularTasks() {
        List<RegularTask> tasks = taskService.getRegularTasks();
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/tasks/recurring/{id}")
    public ResponseEntity<Void> deleteRecurringTask(@PathVariable Long id) {
        taskService.deleteRecurringTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/recurring")
    public ResponseEntity<List<RecurringTask>> getAllRecurringTasks() {
        List<RecurringTask> tasks = taskService.getRecurringTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/recurring/{id}")
    public ResponseEntity<RecurringTask> getRecurringTask(@PathVariable Long id) {
        RecurringTask task = taskService.getRecurringTask(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks/regular/{id}")
    public ResponseEntity<RegularTask> getRegularTask(@PathVariable Long id) {
        RegularTask task = taskService.getRegularTask(id);
        return ResponseEntity.ok(task);
    }
}

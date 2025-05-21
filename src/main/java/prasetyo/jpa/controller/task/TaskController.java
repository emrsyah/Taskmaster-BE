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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import prasetyo.jpa.entity.RecurringTask;
import prasetyo.jpa.entity.RegularTask;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.helper.ResponseHelper;
import prasetyo.jpa.request.task.CreateTaskRequest;
import prasetyo.jpa.service.task.TaskService;
import prasetyo.jpa.middleware.UseMiddleware;

@RestController
@RequestMapping("/api/tasks")  // Added base path
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private ResponseHelper responseHelper;

    @Autowired
    private Validator validator;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @PostMapping
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskRequest request) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        System.out.println("User: " + user);
        if (request.getTaskType() == CreateTaskRequest.TaskType.REGULAR) {
            RegularTask createdTask = taskService.createRegularTaskFromDto(request, user);
            System.out.println("Regular task: " + createdTask);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } else if (request.getTaskType() == CreateTaskRequest.TaskType.RECURRING) {
            System.out.println("Recurring task: " + request);
            RecurringTask createdTask = taskService.createRecurringTaskFromDto(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } else {
            return responseHelper.error("Invalid task type");
        }
    }

    @DeleteMapping("/regular/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Void> deleteRegularTask(@PathVariable Long id) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        taskService.deleteRegularTask(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/regular")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<List<RegularTask>> getAllRegularTasks() {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<RegularTask> tasks = taskService.getRegularTasks(user);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/recurring/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Void> deleteRecurringTask(@PathVariable Long id) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        taskService.deleteRecurringTask(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recurring")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<List<RecurringTask>> getAllRecurringTasks() {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<RecurringTask> tasks = taskService.getRecurringTasks(user);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/recurring/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<RecurringTask> getRecurringTask(@PathVariable Long id) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        RecurringTask task = taskService.getRecurringTask(id, user);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/regular/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<RegularTask> getRegularTask(@PathVariable Long id) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        RegularTask task = taskService.getRegularTask(id, user);
        return ResponseEntity.ok(task);
    }

}
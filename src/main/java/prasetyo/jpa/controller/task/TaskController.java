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
        if (request.getTaskType() == CreateTaskRequest.TaskType.REGULAR) {
            RegularTask createdTask = taskService.createRegularTaskFromDto(request, user);
            return responseHelper.success("Regular task created successfully", createdTask);
        } else if (request.getTaskType() == CreateTaskRequest.TaskType.RECURRING) {
            RecurringTask createdTask = taskService.createRecurringTaskFromDto(request, user);
            return responseHelper.success("Recurring task created successfully", createdTask);
        } else {
            return responseHelper.error("Invalid task type");
        }
    }

    @DeleteMapping("/regular/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> deleteRegularTask(@PathVariable Long id) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        taskService.deleteRegularTask(id, user);
        return responseHelper.success("Regular task deleted successfully");
    }

    @GetMapping("/regular")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> getAllRegularTasks() {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        List<RegularTask> tasks = taskService.getRegularTasks(user);
        return responseHelper.success("Fetched all regular tasks", tasks);
    }

    @DeleteMapping("/recurring/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> deleteRecurringTask(@PathVariable Long id) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        taskService.deleteRecurringTask(id, user);
        return responseHelper.success("Recurring task deleted successfully");
    }

    @GetMapping("/recurring")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> getAllRecurringTasks() {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        List<RecurringTask> tasks = taskService.getRecurringTasks(user);
        return responseHelper.success("Fetched all recurring tasks", tasks);
    }

    @GetMapping("/recurring/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> getRecurringTask(@PathVariable Long id) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        RecurringTask task = taskService.getRecurringTask(id, user);
        return responseHelper.success("Fetched recurring task", task);
    }

    @GetMapping("/regular/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> getRegularTask(@PathVariable Long id) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        RegularTask task = taskService.getRegularTask(id, user);
        return responseHelper.success("Fetched regular task", task);
    }

    @GetMapping("/{id}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> getTask(@PathVariable Long id) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        // Try to find regular task first
        RegularTask regularTask = taskService.getRegularTask(id, user);
        if (regularTask != null) {
            return responseHelper.success("Fetched regular task", regularTask);
        }

        // If regular task not found, try recurring task
        RecurringTask recurringTask = taskService.getRecurringTask(id, user);
        if (recurringTask != null) {
            return responseHelper.success("Fetched recurring task", recurringTask);
        }

        // If neither found, return 404 error
        return responseHelper.error("Task not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> getAllTasks() {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        
        List<RegularTask> regularTasks = taskService.getRegularTasks(user);
        List<RecurringTask> recurringTasks = taskService.getRecurringTasks(user);
        
        Map<String, List<?>> allTasks = Map.of(
            "regularTasks", regularTasks,
            "recurringTasks", recurringTasks
        );
        
        return responseHelper.success("Fetched all tasks", allTasks);
    }

}
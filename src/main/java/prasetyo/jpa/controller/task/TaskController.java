package prasetyo.jpa.controller.task;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import prasetyo.jpa.request.task.UpdateTaskRequest;
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
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Validate based on task type
            if (request.getTaskType() == CreateTaskRequest.TaskType.RECURRING) {
                if (request.getRecurrenceDays() == null || request.getRecurrenceDays().isEmpty()) {
                    return responseHelper.error("Recurrence days are required for recurring tasks", HttpStatus.BAD_REQUEST);
                }
            }

            Map<String, Object> response = new HashMap<>();
            if (request.getTaskType() == CreateTaskRequest.TaskType.REGULAR) {
                RegularTask createdTask = taskService.createRegularTaskFromDto(request, user);
                response.put("status", true);
                response.put("statusCode", HttpStatus.CREATED.value());
                response.put("message", "Regular task created successfully");
                response.put("data", createdTask);
                response.put("errors", null);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else if (request.getTaskType() == CreateTaskRequest.TaskType.RECURRING) {
                RecurringTask createdTask = taskService.createRecurringTaskFromDto(request, user);
                response.put("status", true);
                response.put("statusCode", HttpStatus.CREATED.value());
                response.put("message", "Recurring task created successfully");
                response.put("data", createdTask);
                response.put("errors", null);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return responseHelper.error("Invalid task type", HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            return responseHelper.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/regular/{uuid}")
    @UseMiddleware(names = { "auth" })
    @Deprecated(since = "1.0", forRemoval = true)
    public ResponseEntity<Map<String, Object>> deleteRegularTask(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        taskService.deleteRegularTask(uuid, user);
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

    @DeleteMapping("/recurring/{uuid}")
    @UseMiddleware(names = { "auth" })
    @Deprecated(since = "1.0", forRemoval = true)
    public ResponseEntity<Map<String, Object>> deleteRecurringTask(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        taskService.deleteRecurringTask(uuid, user);
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

    @GetMapping("/recurring/{uuid}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> getRecurringTask(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        RecurringTask task = taskService.getRecurringTask(uuid, user);
        if (task == null) {
            return responseHelper.error("Task not found", HttpStatus.NOT_FOUND);
        }
        return responseHelper.success("Fetched recurring task", task);
    }

    @GetMapping("/regular/{uuid}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> getRegularTask(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        RegularTask task = taskService.getRegularTask(uuid, user);
        if (task == null) {
            return responseHelper.error("Task not found", HttpStatus.NOT_FOUND);
        }
        return responseHelper.success("Fetched regular task", task);
    }

    @GetMapping("/{uuid}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> getTask(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        // Try to find regular task first
        RegularTask regularTask = taskService.getRegularTask(uuid, user);
        if (regularTask != null) {
            return responseHelper.success("Fetched regular task", regularTask);
        }

        // If regular task not found, try recurring task
        RecurringTask recurringTask = taskService.getRecurringTask(uuid, user);
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

    @PutMapping("/{uuid}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> updateTask(@PathVariable String uuid, @Valid @RequestBody UpdateTaskRequest request) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            Object updatedTask = taskService.updateTask(uuid, request, user);
            if (updatedTask == null) {
                return responseHelper.error("Task not found", HttpStatus.NOT_FOUND);
            }

            String taskType = request.getTaskType() == CreateTaskRequest.TaskType.REGULAR ? "regular" : "recurring";
            return responseHelper.success(taskType + " task updated successfully", updatedTask);
        } catch (IllegalArgumentException e) {
            return responseHelper.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{uuid}/start")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> startTask(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        // Try regular task first
        RegularTask regularTask = taskService.getRegularTask(uuid, user);
        if (regularTask != null) {
            regularTask.markInProgress();
            taskService.updateRegularTask(regularTask);
            return responseHelper.success("Regular task started", regularTask);
        }

        // Then try recurring task
        RecurringTask recurringTask = taskService.getRecurringTask(uuid, user);
        if (recurringTask != null) {
            recurringTask.markInProgress();
            taskService.updateRecurringTask(recurringTask);
            return responseHelper.success("Recurring task started", recurringTask);
        }

        return responseHelper.error("Task not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{uuid}/complete")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> completeTask(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        // Try regular task first
        RegularTask regularTask = taskService.getRegularTask(uuid, user);
        if (regularTask != null) {
            regularTask.markCompleted();
            taskService.updateRegularTask(regularTask);
            return responseHelper.success("Regular task completed", regularTask);
        }

        // Then try recurring task
        RecurringTask recurringTask = taskService.getRecurringTask(uuid, user);
        if (recurringTask != null) {
            recurringTask.markCompleted();
            taskService.updateRecurringTask(recurringTask);
            return responseHelper.success("Recurring task completed", recurringTask);
        }

        return responseHelper.error("Task not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{uuid}")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            taskService.deleteTask(uuid, user);
            return responseHelper.success("Task deleted successfully");
        } catch (IllegalArgumentException e) {
            return responseHelper.error("Task not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{uuid}/archive")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> archiveTask(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            Object task = taskService.archiveTask(uuid, user);
            return responseHelper.success("Task archived successfully", task);
        } catch (IllegalArgumentException e) {
            return responseHelper.error("Task not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{uuid}/unarchive")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> unarchiveTask(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            Object task = taskService.unarchiveTask(uuid, user);
            return responseHelper.success("Task unarchived successfully", task);
        } catch (IllegalArgumentException e) {
            return responseHelper.error("Task not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{uuid}/undo-complete")
    @UseMiddleware(names = { "auth" })
    public ResponseEntity<Map<String, Object>> undoTaskCompletion(@PathVariable String uuid) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        if (user == null) {
            return responseHelper.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            Object task = taskService.undoTaskCompletion(uuid, user);
            return responseHelper.success("Task completion undone successfully", task);
        } catch (IllegalArgumentException e) {
            return responseHelper.error("Task not found", HttpStatus.NOT_FOUND);
        }
    }
}
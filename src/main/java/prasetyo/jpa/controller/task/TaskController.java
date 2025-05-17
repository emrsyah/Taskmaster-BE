package prasetyo.jpa.controller.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import prasetyo.jpa.entity.RegularTask;
import prasetyo.jpa.service.task.TaskService;

public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<RegularTask> createTask(@RequestBody RegularTask task) {
        RegularTask createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
}

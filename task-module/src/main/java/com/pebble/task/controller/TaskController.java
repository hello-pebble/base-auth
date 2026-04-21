package com.pebble.task.controller;

import com.pebble.task.domain.Task;
import com.pebble.task.domain.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, @AuthenticationPrincipal Jwt jwt) {
        task.setUserId(jwt.getSubject());
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping
    public ResponseEntity<List<Task>> getMyTasks(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(taskService.getMyTasks(jwt.getSubject()));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam boolean completed,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, jwt.getSubject(), completed));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, @AuthenticationPrincipal Jwt jwt) {
        taskService.deleteTask(taskId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}

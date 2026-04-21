package com.pebble.task.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository taskRepository;

    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getMyTasks(String userId) {
        return taskRepository.findAllByUserId(userId);
    }

    @Transactional
    public Task updateTaskStatus(Long taskId, String userId, boolean completed) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        
        if (!task.getUserId().equals(userId)) {
            throw new IllegalStateException("Not authorized to update this task");
        }
        
        task.setCompleted(completed);
        return task;
    }

    @Transactional
    public void deleteTask(Long taskId, String userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        
        if (!task.getUserId().equals(userId)) {
            throw new IllegalStateException("Not authorized to delete this task");
        }
        
        taskRepository.delete(task);
    }
}

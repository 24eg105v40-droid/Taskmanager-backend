package com.taskmanager.controller;

import com.taskmanager.dto.ApiResponse;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Retrieve all tasks, optionally filtered by status (PENDING or COMPLETED)
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam(required = false) TaskStatus status) {
        List<Task> tasks = taskService.getAllTasks(status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieve a specific task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Get task statistics (total, pending, completed)
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTaskStats() {
        Map<String, Object> stats = taskService.getTaskStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Create a new task
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Task>> createTask(@Valid @RequestBody TaskRequest request) {
        Task createdTask = taskService.createTask(request);
        ApiResponse<Task> response = ApiResponse.success("Task created successfully", createdTask);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update an existing task's title, description, due date, or status
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        Task updatedTask = taskService.updateTask(id, request);
        ApiResponse<Task> response = ApiResponse.success("Task updated successfully", updatedTask);
        return ResponseEntity.ok(response);
    }

    /**
     * Toggle or update a task's status between PENDING and COMPLETED
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Task>> toggleTaskStatus(@PathVariable Long id) {
        Task updatedTask = taskService.toggleTaskStatus(id);
        ApiResponse<Task> response = ApiResponse.success(
                "Task marked as " + updatedTask.getStatus(),
                updatedTask
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a task by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        ApiResponse<Void> response = ApiResponse.success("Task deleted successfully");
        return ResponseEntity.ok(response);
    }
}

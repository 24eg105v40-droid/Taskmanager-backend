package com.taskmanager.service.impl;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasks(TaskStatus status) {
        if (status != null) {
            return taskRepository.findByStatusOrderByCreatedAtDesc(status);
        }
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    @Override
    public Task createTask(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        task.setDueDate(request.getDueDate());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.PENDING);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, TaskRequest request) {
        Task existingTask = getTaskById(id);

        existingTask.setTitle(request.getTitle().trim());
        existingTask.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        existingTask.setDueDate(request.getDueDate());
        if (request.getStatus() != null) {
            existingTask.setStatus(request.getStatus());
        }

        return taskRepository.save(existingTask);
    }

    @Override
    public Task toggleTaskStatus(Long id) {
        Task existingTask = getTaskById(id);
        if (existingTask.getStatus() == TaskStatus.PENDING) {
            existingTask.setStatus(TaskStatus.COMPLETED);
        } else {
            existingTask.setStatus(TaskStatus.PENDING);
        }
        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(Long id) {
        Task existingTask = getTaskById(id);
        taskRepository.delete(existingTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTaskStats() {
        long total = taskRepository.count();
        long pending = taskRepository.countByStatus(TaskStatus.PENDING);
        long completed = taskRepository.countByStatus(TaskStatus.COMPLETED);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("completed", completed);
        return stats;
    }
}

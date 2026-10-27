package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;

import java.util.List;
import java.util.Map;

public interface TaskService {

    List<Task> getAllTasks(TaskStatus status);

    Task getTaskById(Long id);

    Task createTask(TaskRequest request);

    Task updateTask(Long id, TaskRequest request);

    Task toggleTaskStatus(Long id);

    void deleteTask(Long id);

    Map<String, Object> getTaskStats();
}

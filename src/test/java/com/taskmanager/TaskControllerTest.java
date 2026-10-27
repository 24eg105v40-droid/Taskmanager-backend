package com.taskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.controller.TaskController;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task sampleTask1;
    private Task sampleTask2;

    @BeforeEach
    void setUp() {
        sampleTask1 = new Task("Complete Project", "Finish Task Manager application", LocalDate.now(), TaskStatus.PENDING);
        sampleTask1.setId(1L);

        sampleTask2 = new Task("Test REST API", "Verify all endpoints", LocalDate.now(), TaskStatus.COMPLETED);
        sampleTask2.setId(2L);
    }

    @Test
    void testGetAllTasks() throws Exception {
        List<Task> tasks = Arrays.asList(sampleTask1, sampleTask2);
        Mockito.when(taskService.getAllTasks(null)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Complete Project")))
                .andExpect(jsonPath("$[1].title", is("Test REST API")));
    }

    @Test
    void testGetTasksFilteredByStatus() throws Exception {
        List<Task> pendingTasks = List.of(sampleTask1);
        Mockito.when(taskService.getAllTasks(TaskStatus.PENDING)).thenReturn(pendingTasks);

        mockMvc.perform(get("/api/tasks?status=PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }

    @Test
    void testGetTaskById() throws Exception {
        Mockito.when(taskService.getTaskById(1L)).thenReturn(sampleTask1);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Complete Project")));
    }

    @Test
    void testCreateTaskSuccess() throws Exception {
        TaskRequest request = new TaskRequest("New Task", "Description", LocalDate.now(), TaskStatus.PENDING);
        Task createdTask = new Task("New Task", "Description", LocalDate.now(), TaskStatus.PENDING);
        createdTask.setId(3L);

        Mockito.when(taskService.createTask(any(TaskRequest.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Task created successfully")))
                .andExpect(jsonPath("$.data.id", is(3)))
                .andExpect(jsonPath("$.data.title", is("New Task")));
    }

    @Test
    void testCreateTaskValidationFailure() throws Exception {
        // Title is blank - should fail validation
        TaskRequest request = new TaskRequest("", "Description", LocalDate.now(), TaskStatus.PENDING);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.validationErrors.title", notNullValue()));
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskRequest request = new TaskRequest("Updated Task", "Updated Description", LocalDate.now(), TaskStatus.COMPLETED);
        Task updatedTask = new Task("Updated Task", "Updated Description", LocalDate.now(), TaskStatus.COMPLETED);
        updatedTask.setId(1L);

        Mockito.when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Updated Task")))
                .andExpect(jsonPath("$.data.status", is("COMPLETED")));
    }

    @Test
    void testToggleTaskStatus() throws Exception {
        sampleTask1.setStatus(TaskStatus.COMPLETED);
        Mockito.when(taskService.toggleTaskStatus(1L)).thenReturn(sampleTask1);

        mockMvc.perform(patch("/api/tasks/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("COMPLETED")));
    }

    @Test
    void testDeleteTask() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Task deleted successfully")));
    }

    @Test
    void testGetTaskStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 5L);
        stats.put("pending", 3L);
        stats.put("completed", 2L);

        Mockito.when(taskService.getTaskStats()).thenReturn(stats);

        mockMvc.perform(get("/api/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(5)))
                .andExpect(jsonPath("$.pending", is(3)))
                .andExpect(jsonPath("$.completed", is(2)));
    }
}

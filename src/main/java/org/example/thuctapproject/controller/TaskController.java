package org.example.thuctapproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.thuctapproject.model.request.TaskRequest;
import org.example.thuctapproject.model.response.ApiResponse;
import org.example.thuctapproject.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/list")
    @Operation(summary = "List all tasks (Manager)")
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(taskService.getAllTask()));
    }

    @GetMapping("/list-by-user/{id}")
    @Operation(summary = "List tasks by assignee (Manager)")
    public ResponseEntity<ApiResponse<?>> getAllByUser(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskByUserId(id)));
    }

    @GetMapping("/list-by-project/{id}")
    @Operation(summary = "List tasks by project (Manager)")
    public ResponseEntity<ApiResponse<?>> getAllByProject(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskByProjectId(id)));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "Get task detail", description = "Manager can view any task. User can only view their own tasks.")
    public ResponseEntity<ApiResponse<?>> detail(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskById(id), "Detail task with id: " + id));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my tasks", description = "Return tasks assigned to currently authenticated user")
    public ResponseEntity<ApiResponse<?>> myTasks() {
        return ResponseEntity.ok(ApiResponse.success(taskService.getMyTasks()));
    }

    @PostMapping("/add")
    @Operation(summary = "Create task (Manager)")
    public ResponseEntity<ApiResponse<?>> add(@Valid @RequestBody TaskRequest request) {
        taskService.createTask(request);
        return new ResponseEntity<>(ApiResponse.of(201, "Add task successfully", request), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update task (Manager)")
    public ResponseEntity<ApiResponse<?>> update(@Valid @RequestBody TaskRequest request, @PathVariable Integer id) {
        taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(request, "Update task successfully"));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete task (Manager)")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete task successfully"));
    }

    @PutMapping("/assign/{taskId}/{userId}")
    @Operation(summary = "Assign task to user (Manager)", description = "Assignment is allowed only if the user belongs to the same project as the task")
    public ResponseEntity<ApiResponse<?>> assignTask(@PathVariable Integer taskId, @PathVariable Integer userId) {
        taskService.assignTask(taskId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Assign task successfully"));
    }

    @PutMapping("/change-status/{taskId}/{status}")
    @Operation(summary = "Change task status (Manager)", description = "Cannot change status of a completed (DONE) task")
    public ResponseEntity<ApiResponse<?>> changeStatus(@PathVariable Integer taskId, @PathVariable String status) {
        taskService.changeStatus(taskId, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Change status successfully"));
    }
}

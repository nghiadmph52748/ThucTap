package org.example.thuctapproject.controller;

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
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(taskService.getAllTask()));
    }

    @GetMapping("/list-by-user/{id}")
    public ResponseEntity<ApiResponse<?>> getAllByUser(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskByUserId(id)));
    }

    @GetMapping("/list-by-project/{id}")
    public ResponseEntity<ApiResponse<?>> getAllByProject(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskByProjectId(id)));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<?>> detail(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskById(id), "Detail task with id: " + id));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> add(@Valid @RequestBody TaskRequest request) {
        taskService.createTask(request);
        return new ResponseEntity<>(ApiResponse.of(201, "Add task successfully", request), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> update(@Valid @RequestBody TaskRequest request, @PathVariable Integer id) {
        taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(request, "Update task successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete task successfully"));
    }

    @PutMapping("/assign/{taskId}/{userId}")
    public ResponseEntity<ApiResponse<?>> assignTask(@PathVariable Integer taskId, @PathVariable Integer userId) {
        taskService.assignTask(taskId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Assign task successfully"));
    }

    @PutMapping("/change-status/{taskId}/{status}")
    public ResponseEntity<ApiResponse<?>> changeStatus(@PathVariable Integer taskId, @PathVariable String status) {
        taskService.changeStatus(taskId, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Change status successfully"));
    }
}

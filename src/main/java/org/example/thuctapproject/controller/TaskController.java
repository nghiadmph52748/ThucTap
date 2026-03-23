package org.example.thuctapproject.controller;

import jakarta.validation.Valid;
import org.example.thuctapproject.model.request.TaskRequest;
import org.example.thuctapproject.model.response.ResponseObject;
import org.example.thuctapproject.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/list")
    public ResponseObject<?> getAll() {
        return new ResponseObject<>(taskService.getAllTask());
    }

    @GetMapping("/list-by-user/{id}")
    public ResponseObject<?> getAllByUser(@PathVariable Integer id) {
        return new ResponseObject<>(taskService.getTaskByUserId(id));
    }

    @GetMapping("/list-by-project/{id}")
    public ResponseObject<?> getAllByProject(@PathVariable Integer id) {
        return new ResponseObject<>(taskService.getTaskByProjectId(id));
    }

    @GetMapping("/detail/{id}")
    public ResponseObject<?> detail(@PathVariable Integer id) {
        return new ResponseObject<>(taskService.getTaskById(id), "Detail task with id: " + id);
    }

    @PostMapping("/add")
    public ResponseObject<?> add(@Valid @RequestBody TaskRequest request) {
        taskService.createTask(request);
        return new ResponseObject<>(true, request, "Add task successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseObject<?> update(@Valid @RequestBody TaskRequest request, @PathVariable Integer id) {
        taskService.updateTask(id, request);
        return new ResponseObject<>(true, request, "Update task successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseObject<?> delete(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return new ResponseObject<>(true, null, "Delete task successfully");
    }
}

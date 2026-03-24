package org.example.thuctapproject.controller;

import org.example.thuctapproject.model.request.UserRequest;
import org.example.thuctapproject.model.response.ApiResponse;
import org.example.thuctapproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUser()));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<?>> detail(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id), "Detail user with id: " + id));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> add(@RequestBody UserRequest request) {
        userService.createUser(request);
        return new ResponseEntity<>(ApiResponse.of(201, "Add user successfully", request), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> update(@RequestBody UserRequest request, @PathVariable Integer id) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(request, "Update user successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete user successfully"));
    }
}

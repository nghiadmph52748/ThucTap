package org.example.thuctapproject.controller;

import org.example.thuctapproject.model.request.UserRequest;
import org.example.thuctapproject.model.response.ResponseObject;
import org.example.thuctapproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseObject<?> getAll() {
        return new ResponseObject<>(userService.getAllUser());
    }

    @GetMapping("/detail/{id}")
    public ResponseObject<?> detail(@PathVariable Integer id) {
        return new ResponseObject<>(userService.getUserById(id), "Detail user with id: " + id);
    }

    @PostMapping("/add")
    public ResponseObject<?> add(@RequestBody UserRequest request) {
        userService.createUser(request);
        return new ResponseObject<>(true, request, "Add user successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody UserRequest request, @PathVariable Integer id) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(new ResponseObject<>(true, request, "Update user successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseObject<?> delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return new ResponseObject<>(true, null, "Delete user successfully");
    }
}

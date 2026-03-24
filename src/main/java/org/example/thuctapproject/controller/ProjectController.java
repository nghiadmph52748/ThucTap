package org.example.thuctapproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.thuctapproject.model.request.ProjectRequest;
import org.example.thuctapproject.model.response.ApiResponse;
import org.example.thuctapproject.model.response.ProjectResponse;
import org.example.thuctapproject.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/list")
    @Operation(summary = "List projects")
    public ResponseEntity<ApiResponse<?>> getAll() {
        List<ProjectResponse> list = projectService.getAllProject();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/add")
    @Operation(summary = "Create project (Manager)")
    public ResponseEntity<ApiResponse<?>> add(@RequestBody ProjectRequest request) {
        ProjectResponse created = projectService.createProject(request);
        return new ResponseEntity<>(ApiResponse.of(201, "Add project successfully", created), HttpStatus.CREATED);
    }
}

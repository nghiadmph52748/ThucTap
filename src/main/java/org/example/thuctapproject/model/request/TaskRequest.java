package org.example.thuctapproject.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    @NotBlank(message = "title is required")
    private String title;
    @NotBlank(message = "status is required")
    private String status;
    @NotNull(message = "project id is required")
    private Integer project;
    @NotNull(message = "assignee id is required")
    private Integer assignee;
}

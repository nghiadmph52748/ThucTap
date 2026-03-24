package org.example.thuctapproject.model.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    @NotBlank(message = "title is required")
    @Size(min = 3, max = 255, message = "title length must be between 3 and 255 characters")
    private String title;

    @NotBlank(message = "status is required")
    @Size(max = 20, message = "status length must be at most 20 characters")
    private String status;

    @NotNull(message = "project id is required")
    private Integer project;

    @NotNull(message = "assignee id is required")
    private Integer assignee;

    @NotNull(message = "deadline is required")
    @Future(message = "deadline must be greater than current date")
    private LocalDate deadline;
}

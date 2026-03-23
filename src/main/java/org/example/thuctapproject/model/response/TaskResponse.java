package org.example.thuctapproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.thuctapproject.entity.TaskEntity;
import org.example.thuctapproject.entity.TaskStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private String title;
    private TaskStatus status;
    private ProjectResponse project;
    private UserResponse assignee;

    public TaskResponse(TaskEntity task) {
        this.title = task.getTitle();
        this.status = task.getStatus();
        this.project = new ProjectResponse(task.getProject());
        this.assignee = task.getAssignee() != null ? new UserResponse(task.getAssignee()) : null;
    }
}

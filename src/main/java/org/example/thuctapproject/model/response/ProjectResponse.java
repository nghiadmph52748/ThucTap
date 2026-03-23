package org.example.thuctapproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.thuctapproject.entity.ProjectEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private Integer id;
    private String name;

    public ProjectResponse(ProjectEntity project) {
        this.id = project.getId();
        this.name = project.getName();
    }
}

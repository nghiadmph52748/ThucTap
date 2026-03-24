package org.example.thuctapproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ProjectUserId implements Serializable {
    private static final long serialVersionUID = -3077145189085849027L;
    @NotNull
    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProjectUserId entity = (ProjectUserId) o;
        return Objects.equals(this.projectId, entity.projectId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, userId);
    }

}
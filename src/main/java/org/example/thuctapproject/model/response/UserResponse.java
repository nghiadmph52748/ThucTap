package org.example.thuctapproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.thuctapproject.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String fullName;
    private String email;

    public UserResponse(UserEntity user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
    }
}

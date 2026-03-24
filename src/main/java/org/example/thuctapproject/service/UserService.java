package org.example.thuctapproject.service;

import org.example.thuctapproject.entity.UserEntity;
import org.example.thuctapproject.exception.ApiException;
import org.example.thuctapproject.model.request.UserRequest;
import org.example.thuctapproject.model.response.UserResponse;
import org.example.thuctapproject.repository.UserRepository;
import org.example.thuctapproject.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserResponse> getAllUser(){
        return userRepository.findAll().stream().map(UserResponse::new).toList();
    }

    public UserResponse getUserById(Integer id) {
        if (id == null) throw new ApiException("User id must not be null", "400");
        return userRepository.findById(id).map(UserResponse::new)
                .orElseThrow(() -> new ApiException("User not found", "404"));
    }

    public void createUser(UserRequest request){
        UserEntity user = MapperUtils.map(request, UserEntity.class);
        userRepository.save(user);
    }

    public void updateUser(Integer id, UserRequest request){
        if (id == null) throw new ApiException("User id must not be null", "400");
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found", "404"));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        if (id == null) throw new ApiException("User id must not be null", "400");
        userRepository.deleteById(id);
    }
}

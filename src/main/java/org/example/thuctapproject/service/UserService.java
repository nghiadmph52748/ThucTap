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
        return userRepository.findById(id).map(UserResponse::new).orElse(null);
    }

    public void createUser(UserRequest request){
        UserEntity user = MapperUtils.map(request, UserEntity.class);
        userRepository.save(user);
    }

    public void updateUser(Integer id, UserRequest request){
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new ApiException("User not found", "410"));
        if (user != null) {
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            userRepository.save(user);
        }
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}

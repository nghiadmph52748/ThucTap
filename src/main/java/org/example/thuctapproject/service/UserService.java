package org.example.thuctapproject.service;

import org.example.thuctapproject.entity.UserEntity;
import org.example.thuctapproject.exception.ApiException;
import org.example.thuctapproject.model.request.UserRequest;
import org.example.thuctapproject.model.response.UserResponse;
import org.example.thuctapproject.repository.UserRepository;
import org.example.thuctapproject.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUser(){
        return userRepository.findAll().stream().map(UserResponse::new).toList();
    }

    public UserResponse getUserById(Integer id) {
        if (id == null) throw new ApiException("User id must not be null", "400");
        return userRepository.findById(id).map(UserResponse::new)
                .orElseThrow(() -> new ApiException("User not found", "404"));
    }

    public void createUser(UserRequest request){
        if (request.getEmail() == null || request.getEmail().isBlank())
            throw new ApiException("Email must not be blank", "400");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new ApiException("Email already exists", "400");
        UserEntity user = MapperUtils.map(request, UserEntity.class);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
    }

    public void updateUser(Integer id, UserRequest request){
        if (id == null) throw new ApiException("User id must not be null", "400");
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found", "404"));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        if (id == null) throw new ApiException("User id must not be null", "400");
        userRepository.deleteById(id);
    }
}

package org.example.thuctapproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.thuctapproject.entity.RoleEntity;
import org.example.thuctapproject.entity.UserEntity;
import org.example.thuctapproject.entity.UserRoleEntity;
import org.example.thuctapproject.entity.UserRoleId;
import org.example.thuctapproject.exception.ApiException;
import org.example.thuctapproject.model.request.UserRequest;
import org.example.thuctapproject.model.response.ApiResponse;
import org.example.thuctapproject.repository.RoleRepository;
import org.example.thuctapproject.repository.UserRepository;
import org.example.thuctapproject.repository.UserRoleRepository;
import org.example.thuctapproject.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication (login/register) endpoints")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate by email/password and receive a JWT token")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                .collect(Collectors.toList());
        String token = jwtUtil.generateToken(request.getEmail(), roles);
        return ResponseEntity.ok(ApiResponse.success(new AuthResponse(token), "Login successfully"));
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user with default role USER")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already exists", "400");
        }
        UserEntity user = new UserEntity();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        RoleEntity userRole = roleRepository.findByName("USER").orElseThrow(() -> new ApiException("Default role USER not found", "500"));
        UserRoleEntity link = new UserRoleEntity();
        UserRoleId id = new UserRoleId();
        id.setUserId(user.getId());
        id.setRoleId(userRole.getId());
        link.setId(id);
        link.setUser(user);
        link.setRoleEntity(userRole);
        userRoleRepository.save(link);
        return new ResponseEntity<>(ApiResponse.of(201, "Register successfully", null), HttpStatus.CREATED);
    }

    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        public AuthResponse(String token) { this.token = token; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}

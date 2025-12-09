package com.example.authservice.services;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.entities.User;
import com.example.authservice.repositories.UserRepository;
import com.example.authservice.services.UserServiceClient;
import com.example.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Проверка существования пользователя
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new RuntimeException("User with login " + request.getLogin() + " already exists");
        }
        
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("User with email " + request.getEmail() + " already exists");
            }
        }
        
        // Создание нового пользователя
        User user = new User();
        user.setLogin(request.getLogin().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Устанавливаем email только если он не пустой
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(request.getEmail().trim());
        } else {
            user.setEmail(null);
        }
        
        User savedUser = userRepository.save(user);
        
        // Создание пользователя в KursNews service
        try {
            System.out.println("Attempting to create user in KursNews: userId=" + savedUser.getId() + ", login=" + savedUser.getLogin());
            userServiceClient.createUserInKursNews(savedUser.getId(), savedUser.getLogin());
            System.out.println("Successfully created user in KursNews");
        } catch (Exception e) {
            // Логируем ошибку подробно
            System.err.println("Failed to create user in KursNews service: " + e.getMessage());
            e.printStackTrace();
            // Можно также откатить транзакцию, если требуется строгая согласованность
            // throw new RuntimeException("Failed to create user in KursNews service", e);
        }
        
        // Генерация JWT токена
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getLogin());
        
        return new AuthResponse(
            token,
            "Bearer",
            savedUser.getId(),
            savedUser.getLogin(),
            savedUser.getEmail()
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
            .orElseThrow(() -> new RuntimeException("Invalid login or password"));
        
        // Проверка пароля
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid login or password");
        }
        
        // Генерация JWT токена
        String token = jwtUtil.generateToken(user.getId(), user.getLogin());
        
        return new AuthResponse(
            token,
            "Bearer",
            user.getId(),
            user.getLogin(),
            user.getEmail()
        );
    }
    
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}




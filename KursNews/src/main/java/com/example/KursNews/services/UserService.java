package com.example.KursNews.services;

import com.example.KursNews.dto.CommentDto;
import com.example.KursNews.dto.UserDto;
import com.example.KursNews.entities.BaseEntity;
import com.example.KursNews.entities.User;
import com.example.KursNews.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService{

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository repository;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;

    @Transactional
    public UserDto saveUser(UserDto userDto) {
        logger.info("Saving user: id={}, login={}", userDto.getId(), userDto.getLogin());
        
        User user = mapToEntity(userDto);
        
        if (user.getId() != null) {
            logger.info("User ID is provided: {}", user.getId());
            
            if (repository.existsById(user.getId())) {
                logger.info("User already exists, updating: {}", user.getId());
                User existingUser = repository.findById(user.getId()).get();
                existingUser.setLogin(user.getLogin());
                return mapToDto(repository.save(existingUser));
            }

            logger.info("User does not exist, creating with specified ID: {}", user.getId());
            long currentTime = System.currentTimeMillis();
            
            Query query = entityManager.createNativeQuery(
                "INSERT INTO webcoursenews.customer (id, customer_login, insert_at, update_at) " +
                "VALUES (?, ?, ?, ?)"
            );
            query.setParameter(1, user.getId());
            query.setParameter(2, user.getLogin());
            query.setParameter(3, currentTime);
            query.setParameter(4, currentTime);
            
            int result = query.executeUpdate();
            logger.info("Native insert result: {}", result);
            
            User savedUser = repository.findById(user.getId()).orElseThrow(
                () -> new RuntimeException("Failed to create user with id: " + user.getId())
            );
            return mapToDto(savedUser);
        }
        
        logger.info("No ID provided, generating new ID");
        return mapToDto(repository.save(user));
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private User mapToEntity(UserDto dto){
        User user = new User();
        if (dto.getId() != null) {
            user.setId(dto.getId());
        }
        user.setLogin(dto.getLogin());
        return user;
    }

    private UserDto mapToDto(User user){
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setInsertAt(user.getInsertAt());
        dto.setUpdateAt(user.getUpdateAt());
        return dto;
    }

    @Transactional
    public UserDto findById(UUID id) {
        return mapToDto(repository.findById(id).get());
    }

    @Transactional
    public UserDto updateUser(UUID id, UserDto dto) {
        User user = repository.findById(id).get();
        user.setLogin(dto.getLogin());
        return mapToDto(user);
    }

    public List<UserDto> getAll() {
        List<UserDto> dtoList = new ArrayList<>();
        repository.findAll().forEach(user -> {dtoList.add(mapToDto(user));});
        return dtoList;
    }
}

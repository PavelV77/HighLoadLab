package com.example.KursNews.services;

import com.example.KursNews.dto.CommentDto;
import com.example.KursNews.dto.UserDto;
import com.example.KursNews.entities.BaseEntity;
import com.example.KursNews.entities.User;
import com.example.KursNews.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository repository;
    private final ModelMapper modelMapper;

    @Transactional
    public UserDto saveUser(UserDto userDto) {
        return mapToDto(repository.save(mapToEntity(userDto)));
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private User mapToEntity(UserDto dto){
        User user = new User();
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

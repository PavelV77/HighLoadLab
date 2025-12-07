package com.example.KursNews.services;

import com.example.KursNews.dto.LikeDto;
import com.example.KursNews.entities.Like;
import com.example.KursNews.repositories.LikeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public LikeDto createLike(LikeDto likeDto) {
        return modelMapper.map(likeRepository.save(modelMapper.map(likeDto, Like.class)),LikeDto.class);
    }

    public void deleteById(UUID likeId) {
        likeRepository.deleteById(likeId);
    }
}

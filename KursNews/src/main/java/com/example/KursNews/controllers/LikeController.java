package com.example.KursNews.controllers;

import com.example.KursNews.dto.LikeDto;
import com.example.KursNews.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/news")
public class LikeController {
    @Autowired
    private LikeService likeService;
    @PostMapping("/activities")
    public ResponseEntity<LikeDto> addLike(@RequestBody LikeDto likeDto){
        return new ResponseEntity<>(likeService.createLike(likeDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/activities/{likeId}")
    public void deleteLike(@PathVariable("likeId") UUID likeId){
        likeService.deleteById(likeId);
    }
}

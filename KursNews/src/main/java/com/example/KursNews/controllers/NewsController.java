package com.example.KursNews.controllers;

import com.example.KursNews.dto.NewsDto;
import com.example.KursNews.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;
    @PostMapping("/{userId}")
    public ResponseEntity<NewsDto> addNews(@PathVariable UUID userId, @RequestBody NewsDto newsDto) {
        return new ResponseEntity<>(newsService.createNews(userId, newsDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NewsDto>> getAll() {
        return new ResponseEntity<>(newsService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<NewsDto> getNews(@PathVariable("newsId") UUID id) {
        return new ResponseEntity<>(newsService.getNewsById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{newsId}")
    public ResponseEntity<?> deleteNews(@PathVariable("newsId") UUID newsId) {
        newsService.deleteById(newsId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{newsId}")
    public ResponseEntity<NewsDto> putNews(@PathVariable("newsId") UUID newsId, @RequestBody NewsDto newsDto) {
        return new ResponseEntity<>(newsService.updateNews(newsId, newsDto), HttpStatus.OK);
    }

}

package com.example.KursNews.services;

import com.example.KursNews.dto.NewsDto;
import com.example.KursNews.dto.UserDto;
import com.example.KursNews.entities.News;
import com.example.KursNews.repositories.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository repository;
    private final ModelMapper modelMapper;

    public NewsDto createNews(UUID userId, NewsDto newsDto) {
        newsDto.setUserId(userId);
        return modelMapper.map(repository.save(modelMapper.map(newsDto, News.class)), NewsDto.class);
    }

    @Transactional
    public NewsDto getNewsById(UUID id) {
        return modelMapper.map(repository.findById(id).get(), NewsDto.class);
    }

    @Transactional
    public List<NewsDto> findAll() {
        List<NewsDto> dtoList = new ArrayList<>();
        repository.findAll().forEach(news -> dtoList.add(modelMapper.map(news, NewsDto.class)));
        return dtoList;
    }
    @Transactional
    public void deleteById(UUID newsId) {
        repository.deleteById(newsId);
    }
    @Transactional
    public NewsDto updateNews(UUID newsId, NewsDto newsDto) {
        newsDto.setId(newsId);
        News news = repository.findById(newsId).get();
        news.setHead(newsDto.getHead());
        news.setBody(newsDto.getBody());
        return modelMapper.map(news, NewsDto.class);
    }
}

package com.example.KursNews.services;

import com.example.KursNews.dto.CommentDto;
import com.example.KursNews.dto.CommentNotificationEvent;
import com.example.KursNews.dto.UserDto;
import com.example.KursNews.entities.Comment;
import com.example.KursNews.entities.News;
import com.example.KursNews.entities.User;
import com.example.KursNews.repositories.CommentRepository;
import com.example.KursNews.repositories.NewsRepository;
import com.example.KursNews.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CommentRepository repository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Transactional
    public CommentDto createComment(UUID newsId, CommentDto commentDto) {
        commentDto.setNewsId(newsId);
        
        News news = newsRepository.findById(newsId)
            .orElseThrow(() -> new RuntimeException("News not found: " + newsId));
        User user = userRepository.findById(commentDto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found: " + commentDto.getUserId()));
        
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setNews(news);
        comment.setUser(user);
        
        Comment savedComment = repository.save(comment);
        CommentDto savedCommentDto = modelMapper.map(savedComment, CommentDto.class);
        
        sendCommentNotificationEvent(newsId, savedCommentDto);
        
        return savedCommentDto;
    }
    
    private void sendCommentNotificationEvent(UUID newsId, CommentDto commentDto) {
        try {
            News news = newsRepository.findById(newsId).orElse(null);
            if (news != null && news.getUser() != null) {
                User newsAuthor = news.getUser();
                String userEmail = newsAuthor.getLogin();
                log.info("Send notification to {}", userEmail);
                CommentNotificationEvent event = new CommentNotificationEvent(
                    commentDto.getId(),
                    newsId,
                    newsAuthor.getId(),
                    commentDto.getUserId(),
                    commentDto.getBody(),
                    news.getHead() != null ? news.getHead() : "Новость",
                    userEmail,
                    System.currentTimeMillis()
                );
                kafkaProducerService.sendCommentNotification(event);
            }
        } catch (Exception e) {
            System.err.println("Failed to send comment notification to Kafka: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteById(UUID commentId) {
        repository.deleteById(commentId);
    }

    @Transactional
    public CommentDto updateComment(UUID commentId, CommentDto dto) {
        Comment comment = repository.findById(commentId).get();
        comment.setBody(dto.getBody());
        return modelMapper.map(comment,CommentDto.class);
    }

    @Transactional
    public CommentDto getComment(UUID commentId) {
        return modelMapper.map(repository.findById(commentId).get(), CommentDto.class);
    }

    public List<CommentDto> getCommentByNewsId(UUID newsId) {
        List<CommentDto> dtoList = new ArrayList<>();
        repository.findAllByNewsId(newsId).forEach(comment -> {dtoList.add(modelMapper.map(comment,CommentDto.class));});
        return dtoList;
    }
}

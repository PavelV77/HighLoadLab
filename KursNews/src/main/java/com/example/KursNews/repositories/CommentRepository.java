package com.example.KursNews.repositories;

import com.example.KursNews.entities.Comment;
import com.example.KursNews.entities.News;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CommentRepository extends CrudRepository<Comment, UUID>, BaseRepository<Comment>{
    Iterable<Object> findAllByNewsId(UUID newsId);
}

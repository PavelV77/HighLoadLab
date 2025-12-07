package com.example.KursNews.repositories;

import com.example.KursNews.entities.Comment;
import com.example.KursNews.entities.Like;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LikeRepository extends CrudRepository<Like, UUID>, BaseRepository<Like>{
}

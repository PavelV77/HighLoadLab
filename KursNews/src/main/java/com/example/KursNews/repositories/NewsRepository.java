package com.example.KursNews.repositories;

import com.example.KursNews.entities.News;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface NewsRepository extends CrudRepository<News, UUID>, BaseRepository<News> {
}

package com.example.KursNews.repositories;

import com.example.KursNews.entities.BaseEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaSpecificationExecutor<T>, CrudRepository<T, UUID> {

}

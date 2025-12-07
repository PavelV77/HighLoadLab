package com.example.KursNews.entities;


import jakarta.persistence.*;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;

import java.util.UUID;

@Data
@NoArgsConstructor
@MappedSuperclass
@DependsOnDatabaseInitialization
public abstract class BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    @Generated(value = GenerationTime.ALWAYS)
    private long insertAt;
    @Column(nullable = false)
    @Generated(value = GenerationTime.ALWAYS)
    private long updateAt;

}

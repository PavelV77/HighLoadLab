package com.example.KursNews.dto;

import com.example.KursNews.typies.NewsStatusType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.UUID;

@Data
public class NewsDto {
    private UUID id;
    private String head;
    private String body;
    private UUID userId;
    private long insertAt;
    private long updateAt;
    private long countLike;
    private long countDislike;
    private long countView;
}

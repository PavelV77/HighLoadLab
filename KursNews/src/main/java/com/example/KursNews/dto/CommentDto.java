package com.example.KursNews.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
public class CommentDto {
    private UUID id;
    private UUID newsId;
    private UUID userId;
    private String body;
    private long insertAt;
    private long updateAt;
}

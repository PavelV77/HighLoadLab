package com.example.KursNews.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LikeDto {
    private UUID id;
    private int typeOfActivity;
    private UUID userId;
    private UUID newsId;
    private long insertAt;
    private long updateAt;
}

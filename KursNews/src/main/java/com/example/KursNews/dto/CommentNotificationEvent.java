package com.example.KursNews.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentNotificationEvent {
    private UUID commentId;
    private UUID newsId;
    private UUID newsAuthorId;
    private UUID commentAuthorId;
    private String commentBody;
    private String newsTitle;
    private String userEmail;
    private long timestamp;
}


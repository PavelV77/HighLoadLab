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
    private UUID newsAuthorId;  // ID автора новости (кому отправлять уведомление)
    private UUID commentAuthorId;  // ID автора комментария
    private String commentBody;
    private String newsTitle;
    private String userEmail;  // Email автора новости для отправки уведомления
    private long timestamp;
}


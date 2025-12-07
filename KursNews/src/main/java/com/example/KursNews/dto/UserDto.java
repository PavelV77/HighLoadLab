package com.example.KursNews.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String login;
    private long insertAt;
    private long updateAt;

}

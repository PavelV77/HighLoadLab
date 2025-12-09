package com.example.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KursNewsUserDto {

    private UUID id;

    private String login;

    private long insertAt;

    private long updateAt;
}


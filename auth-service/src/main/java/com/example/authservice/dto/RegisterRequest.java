package com.example.authservice.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String login;
    private String password;
    private String email;
}




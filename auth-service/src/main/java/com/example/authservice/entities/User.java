package com.example.authservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "auth_user", schema = "webcoursenews")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private java.util.UUID id;
    
    @Column(name = "login", unique = true, nullable = false)
    private String login;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "insert_at", nullable = false)
    private long insertAt;
    
    @Column(name = "update_at", nullable = false)
    private long updateAt;
    
    @PrePersist
    protected void onCreate() {
        insertAt = System.currentTimeMillis();
        updateAt = System.currentTimeMillis();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateAt = System.currentTimeMillis();
    }
}




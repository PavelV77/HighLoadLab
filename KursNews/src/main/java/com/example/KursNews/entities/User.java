package com.example.KursNews.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "customer",schema = "webcoursenews")
public class User extends BaseEntity {
    @Column(name = "customer_login", unique = true,nullable = false)
    private String login;
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<News> newsCollection = new ArrayList<>();
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Like> likesCollection = new ArrayList<>();
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Comment> commentCollection = new ArrayList<>();

}

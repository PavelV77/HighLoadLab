package com.example.KursNews.entities;


import com.example.KursNews.typies.NewsStatusType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Formula;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "news", schema = "webcoursenews")
public class News extends BaseEntity {

    @Column(nullable = true)
    private String head;

    @Column(nullable = true, length = 65535)
    private String body;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false, columnDefinition = "UUID")
    private User user;

    @OneToMany(mappedBy = "news", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Like> likesCollection = new ArrayList<>();

    @OneToMany(mappedBy = "news", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> commentCollection = new ArrayList<>();

    @Formula(value = "(SELECT count(*) FROM webcoursenews.activity l WHERE(l.news_id = id and l.type_of_activity = 1))")
    private long countLike;
    @Formula(value = "(SELECT count(*) FROM webcoursenews.activity l WHERE(l.news_id = id and l.type_of_activity = 2))")
    private long countDislike;

}

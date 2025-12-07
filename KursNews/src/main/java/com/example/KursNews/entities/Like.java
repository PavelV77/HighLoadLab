package com.example.KursNews.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "activity",schema = "webcoursenews")
public class Like extends BaseEntity {
    /*@Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::newsschema.activity_type_for_likes")
    @Column(name = "type_of_activity",nullable = false, columnDefinition = "newsschema.activity_type_for_likes")
    private ActivityTypeForLikes typeOfActivity;*/
    private int typeOfActivity;
    @ManyToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "id", nullable = false, columnDefinition = "UUID")
    private User user;
    @ManyToOne
    @JoinColumn(name = "news_id",referencedColumnName = "id", nullable =false, columnDefinition = "UUID")
    private News news;
}

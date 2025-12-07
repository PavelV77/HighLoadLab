package com.example.KursNews.entities;


//import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "comment", schema = "webcoursenews")
public class Comment extends BaseEntity {

    @Column(name = "body", length = 65535)
    private String body;
    @ManyToOne
    @JoinColumn(name = "customer_id",/*referencedColumnName = "id", nullable = false,*/ columnDefinition = "UUID"/*, insertable = false,updatable = false*/)
    private User user;
    @ManyToOne
    @JoinColumn(name = "news_id",/*referencedColumnName = "id", nullable =false,*/ columnDefinition = "UUID"/*,insertable = false,updatable = false*/)
    private News news;

}

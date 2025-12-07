package com.example.KursNews.controllers;

import com.example.KursNews.dto.CommentDto;
import com.example.KursNews.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/news")
public class CommentController {
    @Autowired
    private CommentService commentService;


    @PostMapping("/{newsId}/comments")
    public ResponseEntity<CommentDto> addComment(@PathVariable("newsId") UUID newsId, @RequestBody CommentDto commentDto) {
        return new ResponseEntity<>(commentService.createComment(newsId, commentDto), HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") UUID commentId) {
        commentService.deleteById(commentId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/comments/{commentId}")
    public CommentDto putComment(@PathVariable("commentId") UUID commentId, @RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentId, commentDto);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("commentId") UUID commentId){
        return new ResponseEntity<>(commentService.getComment(commentId),HttpStatus.OK);
    }

    @GetMapping("{newsId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentByNewsId(@PathVariable("newsId") UUID newsId){
        return new ResponseEntity<>(commentService.getCommentByNewsId(newsId),HttpStatus.OK);
    }


}

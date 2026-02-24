package com.threadly.controller;

import com.threadly.dto.CommentResponse;
import com.threadly.dto.CreateCommentRequest;
import com.threadly.entity.Comment;
import com.threadly.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Operation(summary = "Post a comment on a post")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@PathVariable Long postId, @RequestBody CreateCommentRequest createCommentRequest, Authentication authentication) {
        String username = authentication.getName();
        return commentService.CreateComment(postId, createCommentRequest, username);
    }

    @Operation(summary = "get comments of a post by Id")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }

    @Operation(summary = "Delete current user's comment")
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId, @PathVariable Long postId, Authentication authentication) {
        commentService.delete(postId, commentId, authentication.getName());
    }

}

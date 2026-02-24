package com.threadly.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private String authorUsername;
    private String profileImageUrl;
    private LocalDateTime createdAt;

}
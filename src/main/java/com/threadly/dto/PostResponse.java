package com.threadly.dto;

import com.threadly.VoteType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Long upVotes;
    private Long downVotes;
    private VoteType userVoteType;
    private Long commentCount;

    private String author;
    private LocalDateTime createdAt;
}

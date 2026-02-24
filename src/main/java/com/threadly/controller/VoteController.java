package com.threadly.controller;

import com.threadly.VoteType;
import com.threadly.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/vote")
public class VoteController {
    @Autowired
    private VoteService voteService;

    @Operation(summary = "upvote or downvote a post")
    @PostMapping
    public void vote(@PathVariable Long postId, @RequestParam VoteType voteType, Authentication authentication) {
        voteService.vote(postId, authentication.getName(), voteType);
    }
}

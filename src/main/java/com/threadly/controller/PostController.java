package com.threadly.controller;

import com.threadly.dto.CreatePostRequest;
import com.threadly.dto.PostResponse;
import com.threadly.entity.Post;
import com.threadly.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(summary = "Get user feed.")
    @GetMapping
    public Page<PostResponse> getFeed(@RequestParam(defaultValue = "0") int page,  @RequestParam(defaultValue = "10") int size, Authentication authentication) {
        return postService.getFeed(page, size, authentication.getName());
    }

    @Operation(summary = "Get user feed according to following users")
    @GetMapping("/feed/following")
    public Page<PostResponse> getFollowingFeed( @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Authentication authentication) {
        return postService.getFollowingFeed(authentication.getName(), page, size);
    }

    @GetMapping("/tags/{tag}")
    public Page<PostResponse> getPostsByTag(
            @PathVariable String tag,
            @RequestParam int page,
            @RequestParam int size) {
        return postService.getPostsByTag(tag, page, size);
    }

    @Operation(summary = "Create a new Post")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Post post(@Valid @ModelAttribute CreatePostRequest createPostRequest, Authentication authentication) {
        String name = authentication.getName();
        return postService.createPost(createPostRequest, name);
    }

    @Operation(summary = "Delete a existing post of current user")
    @DeleteMapping("{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("postId") Long postId, Authentication authentication) {
        postService.delete(postId, authentication.getName());
    }

    @GetMapping("/feed/trending")
    public List<PostResponse> getTrendingFeed(@RequestParam(defaultValue = "10") int limit) {
        return postService.getTrendingFeed(limit);
    }
}

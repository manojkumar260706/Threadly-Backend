package com.threadly.controller;

import com.threadly.dto.PostResponse;
import com.threadly.dto.UpdateProfileRequest;
import com.threadly.dto.UserProfileResponse;
import com.threadly.service.FollowService;
import com.threadly.service.PostService;
import com.threadly.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private FollowService followService;

    @Operation(summary = "Update user profile with bio, profile pic or Username")
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void update(@ModelAttribute UpdateProfileRequest updateProfileRequest, Authentication authentication) {
        userService.updateProfile(authentication.getName(), updateProfileRequest);
    }

    @Operation(summary = "Get User profile")
    @GetMapping("/{username}")
    public UserProfileResponse getUserProfile(@PathVariable String username, Authentication authentication) {
        return userService.getUserProfileByUsername(authentication.getName(), username);
    }

    @Operation(summary = "Get User Posts")
    @GetMapping("/{username}/posts")
    public Page<PostResponse> getUserPosts(@PathVariable String username, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return postService.getPostsByUsername(username, page, size);
    }

    @Operation(summary = "Follow other user with other user's id.")
    @PostMapping("/{targetId}/follow")
    public void follow(@PathVariable Long targetId, Authentication authentication) {
        String username = authentication.getName();
        followService.toggleFollow(username, targetId);
    }

    @Operation(summary = "Delete Current user.")
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication) {
        userService.delete(authentication.getName());
    }
}

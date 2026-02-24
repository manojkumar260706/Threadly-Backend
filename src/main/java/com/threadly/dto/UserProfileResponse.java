package com.threadly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    Long id;
    String username;
    String bio;
    String profileImageUrl;
    long followersCount;
    long followingCount;
    boolean isFollowing;
}

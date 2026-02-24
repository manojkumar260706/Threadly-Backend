package com.threadly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResponse {
    List<PostResponse> posts;
    List<UserProfileResponse> userProfiles;
    List<String> hashTags;
}

package com.threadly.service;

import com.threadly.dto.PostResponse;
import com.threadly.dto.SearchResponse;
import com.threadly.dto.UserProfileResponse;
import com.threadly.entity.Post;
import com.threadly.entity.Tag;
import com.threadly.entity.User;
import com.threadly.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SearchService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Transactional(readOnly = true)
    public SearchResponse search(String query) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(query);
        List<Post> posts = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query);
        List<String> hashtags = tagRepository
                .findTop10ByNameContainingIgnoreCase(query).stream()
                .map(Tag::getName)
                .toList();


        return new SearchResponse(
                posts.stream().map(this::mapPost).toList(),
                users.stream().map(this::mapUser).toList(),
                hashtags
        );
    }

    private UserProfileResponse mapUser(User user) {
        long followers = followRepository.countByFollower_Id(user.getId());
        long following = followRepository.countByFollowing_Id(user.getId());

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getBio(),
                user.getProfileImageUrl(),
                followers,
                following,
                false
        );
    }

    private PostResponse mapPost(Post post) {
        String upKey = "post:" + post.getId() + ":up";
        String downKey = "post:" + post.getId() + ":down";

        Long upvotes = Optional.ofNullable(redisTemplate.opsForValue().get(upKey))
                .map(Long::valueOf)
                .orElse(0L);

        Long downvotes = Optional.ofNullable(redisTemplate.opsForValue().get(downKey))
                .map(Long::valueOf)
                .orElse(0L);

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                upvotes,
                downvotes,
                null,
                commentRepository.countByPost(post),
                post.getAuthor().getUsername(),
                post.getCreatedAt()
        );
    }
}

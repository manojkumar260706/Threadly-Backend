package com.threadly.service;

import com.threadly.VoteType;
import com.threadly.entity.Post;
import com.threadly.entity.PostVote;
import com.threadly.entity.User;
import com.threadly.exception.ApiException;
import com.threadly.repository.PostRepository;
import com.threadly.repository.PostVoteRepository;
import com.threadly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private NotificationService notificationService;


    @Transactional
    public void vote(Long postId, String username, VoteType newVoteType) {
        System.out.println("Voting: postId=" + postId + ", user=" + username + ", type=" + newVoteType);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ApiException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException("Post not found"));

        Optional<PostVote> existingVote = postVoteRepository.findByPost_IdAndUser_Id(postId, user.getId());

        String upKey = "post:" + postId + ":up";
        String downKey = "post:" + postId + ":down";

        if(existingVote.isEmpty()){
            PostVote postVote = new PostVote();
            postVote.setPost(post);
            postVote.setUser(user);
            postVote.setVoteType(newVoteType);
            postVoteRepository.save(postVote);

            increment(newVoteType, upKey, downKey, post);

            return;
        }

        PostVote existing = existingVote.get();

        if (existing.getVoteType() == newVoteType) {
            postVoteRepository.delete(existing);
            decrement(newVoteType, upKey, downKey);
        } else {
            VoteType oldType = existing.getVoteType();
            existing.setVoteType(newVoteType);
            postVoteRepository.save(existing);

            decrement(oldType, upKey, downKey);
            increment(newVoteType, upKey, downKey, post);

        }
    }

    private void increment(VoteType type, String upKey, String downKey, Post post) {
        if (type == VoteType.UP) {
            redisTemplate.opsForValue().increment(upKey);
            User user = userRepository.findByUsername(post.getAuthor().getUsername()).orElseThrow(() -> new ApiException("User not found"));
            notificationService.createNotification(user, post.getAuthor().getUsername() +  " up voted your post");
        } else {
            redisTemplate.opsForValue().increment(downKey);
        }
    }

    private void decrement(VoteType type, String upKey, String downKey) {
        if (type == VoteType.UP) {
            redisTemplate.opsForValue().decrement(upKey);
        } else {
            redisTemplate.opsForValue().decrement(downKey);
        }
    }
}

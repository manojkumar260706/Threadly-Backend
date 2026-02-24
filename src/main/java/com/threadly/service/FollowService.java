package com.threadly.service;

import com.threadly.entity.Follow;
import com.threadly.entity.User;
import com.threadly.exception.ApiException;
import com.threadly.repository.FollowRepository;
import com.threadly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public void toggleFollow(String followerName, Long targetId) {
        User follower = userRepository.findByUsername(followerName).orElseThrow(() -> new ApiException("Follower not found"));
        User target = userRepository.findById(targetId).orElseThrow(() -> new ApiException("Target not found"));

        if(follower.equals(target)) {
            throw new ApiException("Follower and Target are the same");
        }

        followRepository.findByFollower_IdAndFollowing_Id(follower.getId(),  target.getId())
                .ifPresentOrElse(existing -> {
                   followRepository.delete(existing);
                }, () -> {
                    Follow follow = Follow.builder()
                            .follower(follower)
                            .following(target)
                            .createdAt(LocalDateTime.now())
                            .build();
                    followRepository.save(follow);
                    notificationService.createNotification(target, follower.getUsername() + " followed you.");
                });
    }
}

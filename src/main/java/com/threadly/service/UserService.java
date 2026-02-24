package com.threadly.service;

import com.threadly.dto.UpdateProfileRequest;
import com.threadly.dto.UserProfileResponse;
import com.threadly.entity.User;
import com.threadly.exception.ApiException;
import com.threadly.repository.CommentRepository;
import com.threadly.repository.FollowRepository;
import com.threadly.repository.PostRepository;
import com.threadly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private FollowRepository followRepository;

    @Transactional
    public void updateProfile(String currentName, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(currentName).orElseThrow(() -> new ApiException("User not found"));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {
            String url = imageUploadService.uploadImage(image);
            user.setProfileImageUrl(url);
        }

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileByUsername(String currentUser, String targetUser) {
        User current = userRepository.findByUsername(targetUser).orElseThrow(() -> new ApiException("Current user not found"));
        User target = userRepository.findByUsername(targetUser).orElseThrow(() -> new ApiException("Target user not found"));

        long followers = followRepository.countByFollower_Id(target.getId());
        long following = followRepository.countByFollowing_Id(target.getId());

        boolean isFollowing = followRepository.existsByFollowerAndFollowing(current, target);

        return new UserProfileResponse(
                target.getId(),
                target.getUsername(),
                target.getBio(),
                target.getProfileImageUrl(),
                followers,
                following,
                isFollowing
        );
    }

    @Transactional
    public void delete(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ApiException("User not found"));
        userRepository.delete(user);
    }
}

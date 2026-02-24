package com.threadly.repository;

import com.threadly.entity.Follow;
import com.threadly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    long countByFollowing_Id(Long followingId);

    long countByFollower_Id(Long followerId);

    boolean existsByFollowerAndFollowing(User follower, User following);
}

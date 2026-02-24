package com.threadly.repository;

import com.threadly.entity.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostVoteRepository extends JpaRepository<PostVote, Long> {
    Optional<PostVote> findByPost_IdAndUser_Id(Long postId, Long userId);

    Optional<PostVote> findByPost_IdAndUser_Username(Long postId, String username);
}

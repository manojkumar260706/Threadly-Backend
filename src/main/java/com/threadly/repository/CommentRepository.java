package com.threadly.repository;

import com.threadly.dto.CommentResponse;
import com.threadly.entity.Comment;
import com.threadly.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Long postId);
    void deleteByPost(Post post);
    long countByPost(Post post);
}

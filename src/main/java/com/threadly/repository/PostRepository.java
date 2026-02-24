package com.threadly.repository;

import com.threadly.entity.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    Page<Post> findAll(Pageable pageable);

    Optional<Post> findById(Long postId);

    @Query("""
    SELECT p FROM Post p
    WHERE p.author.id IN (
        SELECT f.following.id FROM Follow f
        WHERE f.follower.username = :username
    )
    ORDER BY p.createdAt DESC
""")
    Page<Post> findFeedPosts(@Param("username") String username, Pageable pageable);

    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    Page<Post> findByTags_Name(String tagName, Pageable pageable);

    List<Post> findTop50ByOrderByCreatedAtDesc();

    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String query1, String query2);
}

package com.threadly.service;

import com.threadly.dto.CommentResponse;
import com.threadly.dto.CreateCommentRequest;
import com.threadly.entity.Comment;
import com.threadly.entity.Post;
import com.threadly.entity.User;
import com.threadly.exception.ApiException;
import com.threadly.repository.CommentRepository;
import com.threadly.repository.PostRepository;
import com.threadly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public CommentResponse CreateComment(Long postId, CreateCommentRequest createCommentRequest, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException("Post not found"));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ApiException("User not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(createCommentRequest.getComment());
        comment.setAuthor(user);

        Comment saved =  commentRepository.save(comment);
        notificationService.createNotification(post.getAuthor(), user.getUsername() + " commented on your post.");


        return new CommentResponse(saved.getId(), saved.getContent(), saved.getAuthor().getUsername(), saved.getAuthor().getProfileImageUrl(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(@PathVariable Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(postId);

        return comments.stream().map(
                comment -> new CommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthor().getUsername(),
                        comment.getAuthor().getProfileImageUrl(),
                        comment.getCreatedAt()
                )
        ).toList();
    }

    @Transactional
    public void delete(Long postId, Long commentId, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException("Post not found"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException("Comment not found"));

        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new  ApiException("Unauthorized");
        }

        commentRepository.delete(comment);
    }
}

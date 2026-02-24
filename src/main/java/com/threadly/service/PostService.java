package com.threadly.service;

import com.threadly.VoteType;
import com.threadly.dto.CreatePostRequest;
import com.threadly.dto.PostResponse;
import com.threadly.entity.Post;
import com.threadly.entity.PostVote;
import com.threadly.entity.Tag;
import com.threadly.entity.User;
import com.threadly.exception.ApiException;
import com.threadly.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private TagRepository  tagRepository;

    @Transactional(readOnly = true)
    public Page<PostResponse>  getFeed(int page, int size, String username) {
        PageRequest pageRequest = PageRequest.of(page, size,  Sort.by(Sort.Direction.DESC, "createdAt"));


        return postRepository.findAll(pageRequest).map(post -> {
            String upKey = "post:" + post.getId() + ":up";
            String downKey = "post:" + post.getId() + ":down";

            Long upvotes = Optional.ofNullable(redisTemplate.opsForValue().get(upKey))
                    .map(Long::valueOf)
                    .orElse(0L);

            Long downvotes = Optional.ofNullable(redisTemplate.opsForValue().get(downKey))
                    .map(Long::valueOf)
                    .orElse(0L);

            VoteType userVote = postVoteRepository.findByPost_IdAndUser_Username(post.getId(), username).map(PostVote::getVoteType).orElse(null);

            return new PostResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getImageUrl(),
                    upvotes,
                    downvotes,
                    userVote,
                    commentRepository.countByPost(post),
                    post.getAuthor().getUsername(),
                    post.getCreatedAt()
            );
        });
    }

    public Post createPost(CreatePostRequest createPostRequest, String username) {
        User author =  userRepository.findByUsername(username).orElseThrow(() -> new ApiException("User not found"));

        Post post = new Post();
        post.setTitle(createPostRequest.getTitle());
        post.setContent(createPostRequest.getContent());

        MultipartFile image = createPostRequest.getImage();
        if(image != null && !image.isEmpty()) {
            post.setImageUrl(imageUploadService.uploadImage(image));
        }
        post.setAuthor(author);
        Set<String> tagNames = extractTags(post.getContent());

        Set<Tag> tagEntities = tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name))))
                .collect(Collectors.toSet());

        post.setTags(tagEntities);
        updateTrendingTags(tagEntities);
        return postRepository.save(post);
    }

    private void updateTrendingTags(Set<Tag> tags) {
        for (Tag tag : tags) {
            redisTemplate.opsForZSet()
                    .incrementScore("trending:tags", tag.getName(), 1);
        }
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByTag(String tagName, int page, int size) {
        Tag tag = tagRepository.findByName(tagName.toLowerCase())
                .orElseThrow(() -> new ApiException("Tag not found"));

        return postRepository.findByTags_Name(tag.getName(), PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    private PostResponse mapToResponse(Post post) {
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

    private Set<String> extractTags(String content) {
        Pattern pattern = Pattern.compile("(?i)#([a-z0-9_\\-]+)");
        Matcher matcher = pattern.matcher(content);

        Set<String> tags = new HashSet<>();
        while (matcher.find()) {
            tags.add(matcher.group(1).toLowerCase());
        }
        return tags;
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getFollowingFeed(String username, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return postRepository.findFeedPosts(username, pageRequest).map(post -> {
            String upKey = "post:" + post.getId() + ":up";
            String downKey = "post:" + post.getId() + ":down";

            Long upvotes = Optional.ofNullable(redisTemplate.opsForValue().get(upKey))
                    .map(Long::valueOf)
                    .orElse(0L);

            Long downvotes = Optional.ofNullable(redisTemplate.opsForValue().get(downKey))
                    .map(Long::valueOf)
                    .orElse(0L);

            VoteType userVote = postVoteRepository.findByPost_IdAndUser_Username(post.getId(), username).map(PostVote::getVoteType).orElse(null);

            return new PostResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getImageUrl(),
                    upvotes,
                    downvotes,
                    userVote,
                    commentRepository.countByPost(post),
                    post.getAuthor().getUsername(),
                    post.getCreatedAt()
            );
        });
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByUsername(String username, int page, int size) {

        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ApiException("User not found"));



        return postRepository.findByAuthorId(user.getId(), pageable).map( post -> {
            String upKey = "post:" + post.getId() + ":up";
            String downKey = "post:" + post.getId() + ":down";

            Long upvotes = Optional.ofNullable(redisTemplate.opsForValue().get(upKey))
                    .map(Long::valueOf)
                    .orElse(0L);

            Long downvotes = Optional.ofNullable(redisTemplate.opsForValue().get(downKey))
                    .map(Long::valueOf)
                    .orElse(0L);

            VoteType userVote = postVoteRepository.findByPost_IdAndUser_Username(post.getId(), username).map(PostVote::getVoteType).orElse(null);

            return new PostResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getImageUrl(),
                    upvotes,
                    downvotes,
                    userVote,
                    commentRepository.countByPost(post),
                    post.getAuthor().getUsername(),
                    post.getCreatedAt()
            );
        });
    }

    @Transactional
    public void delete(Long postId, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException("Post not found"));

        if(!post.getAuthor().getUsername().equals(username)) {
            throw new ApiException("Unauthorized");
        }

        commentRepository.deleteByPost(post);
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getTrendingFeed(int limit) {
        List<Post> posts = postRepository.findTop50ByOrderByCreatedAtDesc(); // recent pool

        return posts.stream()
                .sorted((p1, p2) -> Long.compare(getScore(p2.getId()), getScore(p1.getId())))
                .limit(limit)
                .map(this::mapToResponse)
                .toList();
    }

    private long getScore(Long postId) {
        String upKey = "post:" + postId + ":up";
        String downKey = "post:" + postId + ":down";

        String up = redisTemplate.opsForValue().get(upKey);
        String down = redisTemplate.opsForValue().get(downKey);

        long upVotes = up != null ? Long.parseLong(up) : 0;
        long downVotes = down != null ? Long.parseLong(down) : 0;

        return upVotes - downVotes;
    }

}

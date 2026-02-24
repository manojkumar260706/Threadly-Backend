package com.threadly.entity;

import com.threadly.VoteType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "post_votes", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
@Data
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType voteType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

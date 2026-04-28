package com.blog.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "likes",   // ✅ VERY IMPORTANT FIX
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","post_id"})
)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}
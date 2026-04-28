package com.blog.model;

import jakarta.persistence.*;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    public Comment() {}

    public Long getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    @ManyToOne
    private Comment parent; // reply

    public Comment getParent() { return parent; }
    public void setParent(Comment parent) { this.parent = parent; }
}

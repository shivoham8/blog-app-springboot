package com.blog.service;

import com.blog.model.Post;
import com.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository repo;

    public List<Post> getAllPosts() {
        return repo.findAll();
    }

    public void save(Post post) {
        repo.save(post);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Post getById(Long id) {
        return repo.findById(id).orElse(null);
    }
    public List<Post> search(String keyword) {
        return repo.findByTitleContainingIgnoreCase(keyword);
    }
}

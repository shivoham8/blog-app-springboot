package com.blog.controller;

import com.blog.model.Comment;
import com.blog.model.Like;
import com.blog.model.Post;
import com.blog.model.User;
import com.blog.repository.CommentRepository;
import com.blog.repository.LikeRepository;
import com.blog.repository.UserRepository;
import com.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class PostController {
    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private PostService service;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private LikeRepository likeRepo;

    @GetMapping("/")
    public String home(Model model, java.security.Principal principal) {
        model.addAttribute("posts", service.getAllPosts());
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        return "index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("post", new Post());
        return "create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Post post) {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepo.findByUsername(username.toLowerCase()).orElse(null);

        post.setUser(user);

        service.save(post);

        return "redirect:/";
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = service.getById(id);

//        Top-level comments
        var comments = commentRepo.findByPostIdAndParentIsNull(id);

        // Replies
        var allReplies = commentRepo.findByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("allReplies", allReplies);
        return "view";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        Post post = service.getById(id);

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 🔥 Check ownership
        if (!post.getUser().getUsername().equals(username.toLowerCase())) {
            return "redirect:/"; // block unauthorized
        }

        service.delete(id);

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {

        Post post = service.getById(id);

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 🔥 Check ownership
        if (!post.getUser().getUsername().equals(username.toLowerCase())) {
            return "redirect:/"; // block unauthorized
        }

        model.addAttribute("post", post);
        return "create";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {

        String name = user.getUsername().trim();

        // Capitalize each word
        String[] parts = name.split(" ");
        StringBuilder formatted = new StringBuilder();

        for (String p : parts) {
            if (!p.isEmpty()) {
                formatted.append(Character.toUpperCase(p.charAt(0)))
                        .append(p.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        String displayName = formatted.toString().trim();

        user.setUsername(displayName.toLowerCase());

        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);

        return "redirect:/login";
    }

    @PostMapping("/comment/{postId}")
    public String addComment(@PathVariable Long postId, @RequestParam String content) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepo.findByUsername(username).orElse(null);
        Post post = service.getById(postId);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setPost(post);

        commentRepo.save(comment);

        return "redirect:/post/" + postId;
    }

    @PostMapping("/like/{postId}")
    public String toggleLike(@PathVariable Long postId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username).orElse(null);
        Post post = service.getById(postId);

        var existing = likeRepo.findByUserIdAndPostId(user.getId(), postId);

        if (existing.isPresent()) {
            likeRepo.delete(existing.get()); // unlike
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepo.save(like);
        }

        return "redirect:/";
    }
    @PostMapping("/reply/{commentId}")
    public String reply(@PathVariable Long commentId,
                        @RequestParam String content) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username).orElse(null);

        Comment parent = commentRepo.findById(commentId).orElse(null);

        Comment reply = new Comment();
        reply.setContent(content);
        reply.setUser(user);
        reply.setPost(parent.getPost());
        reply.setParent(parent);

        commentRepo.save(reply);

        return "redirect:/post/" + parent.getPost().getId();
    }
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("posts", service.search(keyword));
        return "index";
    }
}

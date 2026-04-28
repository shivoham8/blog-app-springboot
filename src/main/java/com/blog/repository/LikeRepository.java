package com.blog.repository;

import com.blog.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    long countByPostId(Long postId);
}
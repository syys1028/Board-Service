package com.backend.board_service.repository;

import com.backend.board_service.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findById(Long id);                           // 1. 게시글 상세 조회 (게시글 id)

    Page<Post> findByUserId(Long userId, Pageable pageable);    // 2. 게시글 상세 조회 (유저 id)

    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Post> searchPosts(@Param("keyword") String keyword);   // 3. 특정 키워드 게시글 검색

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.likes = :likes, p.version = p.version + 1 WHERE p.id = :id AND p.version = :version")
    int updatePostLike(@Param("id") Long id, @Param("likes") Integer likes, @Param("version") Long version);  // 4. 게시글 좋아요 업데이트
}

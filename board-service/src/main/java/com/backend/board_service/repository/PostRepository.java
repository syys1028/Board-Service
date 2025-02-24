package com.backend.board_service.repository;

import com.backend.board_service.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post savePost(Post post);                       // 1. 게시글 작성
    List<Post> findAll();                           // 2. 게시글 목록 조회
    Optional<Post> findByEmail(String email);       // 3. 게시글 상세 조회
    void updatePost(Long id, Post post);            // 4. 게시글 수정 -> Service? DTO 사용?
    void deletePost(Long id);                       // 5. 게시글 삭제
}

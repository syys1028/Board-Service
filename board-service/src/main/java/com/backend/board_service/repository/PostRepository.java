package com.backend.board_service.repository;

import com.backend.board_service.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post savePost(Post post);                           // 1. 게시글 작성
    List<Post> findAll();                               // 2. 게시글 목록 조회
    Optional<Post> findByPostId(Long id);               // 3-1. 게시글 상세 조회 (게시글 id)
    Optional<Post> findByUserId(Long userId);               // 3-2. 게시글 상세 조회 (유저 id)
    void updatePost(Long id, String title, String contents, Integer likes);     // 4. 게시글 수정
    void deletePost(Long id);                               // 5. 게시글 삭제
    void updatePostLike(Long id, Integer likes);            // 6. 게시글 좋아요 업데이트
}

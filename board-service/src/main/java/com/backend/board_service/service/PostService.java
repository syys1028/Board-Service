package com.backend.board_service.service;

import com.backend.board_service.dto.PostDTO;
import com.backend.board_service.entity.Post;
import com.backend.board_service.entity.User;
import com.backend.board_service.exception.UserNotFoundException;
import com.backend.board_service.repository.JPAPostRepository;
import com.backend.board_service.repository.JPAUserRepository;
import com.backend.board_service.repository.PostRepository;
import com.backend.board_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final JPAPostRepository postRepository;
    private final JPAUserRepository userRepository;

    // 1. 게시글 작성
    public Long addPost(PostDTO dto) {
        // 사용자가 존재하는지 먼저 확인
        Optional<User> userId = userRepository.findById(dto.getUserID());
        if (userId.isEmpty()) {
            throw new UserNotFoundException("해당 사용자가 존재하지 않습니다.");
        }
        User user = userId.get();

        LocalDateTime createdAt = dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now();
        PostDTO newDto = dto.toBuilder()
                .createdAt(createdAt)
                .build();

        // Post 엔티티 생성 (User 객체 주입)
        Post post = Post.fromRegisterDTO(newDto, user);
        return postRepository.savePost(post).getId();
    }

    // 2. 게시글 목록 조회
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    // 3-1. 게시글 상세 조회 (게시글 검색)
    public Optional<PostDTO> findPostByPostId(Long postId) {
        return postRepository.findByPostId(postId)
                .map(post -> new PostDTO(
                        post.getTitle(),
                        post.getContents(),
                        post.getUser().getId(),
                        post.getCreatedAt(),
                        post.getLikes()
                ));
    }

    // 3-2. 게시글 상세 조회 (작성자 검색)
    public List<Post> findPostByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    // 4. 게시글 수정
    public boolean updatePost(Long id, String title, String contents, Integer likes) {
        Optional<Post> existingPost = postRepository.findByPostId(id);
        if (existingPost.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 게시물이 존재하지 않습니다.");
        }

        // 기존 데이터와 동일하면 업데이트 안 함
        Post post = existingPost.get();
        if (post.getTitle().equals(title) && post.getContents().equals(contents) && post.getLikes().equals(likes)) {
            return false;
        }

        // 수정된 새 Post 객체 생성
        Post updatedPost = post.toBuilder()
                .title(title)
                .contents(contents)
                .likes(likes)
                .build();
        postRepository.updatePost(updatedPost.getId(), updatedPost);
        return true;
    }

    // 5. 게시글 삭제
    public boolean deletePost(Long id) {
        Optional<Post> post = postRepository.findByPostId(id);
        if (post.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 게시물이 존재하지 않습니다.");
        }

        postRepository.deletePost(id);
        return true;
    }

    // 6. 좋아요 수 업데이트
    public boolean updatePostLike(Long id, Integer likes) {
        Optional<Post> postId = postRepository.findByPostId(id);
        if (postId.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 게시물이 존재하지 않습니다.");
        }

        // 나중에 좋아요 증가, 감소 기능 추가..
        Post post = postId.get();
        if (post.getLikes().equals(likes)) {
            return false;
        }
        Post updatedPost = post.toBuilder()
                .likes(likes)
                .build();
        postRepository.updatePostLike(updatedPost.getId(), likes);  // <- 좋아요만 업데이트
        return true;
    }
}

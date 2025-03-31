package com.backend.board_service.service;

import com.backend.board_service.dto.post.PostDTO;
import com.backend.board_service.entity.Post;
import com.backend.board_service.entity.User;
import com.backend.board_service.exception.UserNotFoundException;
import com.backend.board_service.repository.PostRepository;
import com.backend.board_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 1. 게시글 작성
    public Long addPost(PostDTO dto) {
        // 사용자가 존재하는지 먼저 확인
        User user = userRepository.findById(dto.getUserID())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));

        Post post = Post.builder()
                .title(dto.getTitle())
                .contents(dto.getContents())
                .user(user)
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
                .likes(dto.getLikes() != null ? dto.getLikes() : 0)
                .build();

        // Post 엔티티 생성 (User 객체 주입)
        return postRepository.save(post).getId();
    }

    // 2. 게시글 목록 조회 (페이지네이션)
    public Page<Post> findAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    // 3-1. 게시글 상세 조회 (게시글 검색)
    public Optional<PostDTO> findPostByPostId(Long postId) {
        return postRepository.findById(postId)
                .map(post -> new PostDTO(
                        post.getTitle(),
                        post.getContents(),
                        post.getUser().getId(),
                        post.getCreatedAt(),
                        post.getLikes()
                ));
    }

    // 3-2. 게시글 상세 조회 (유저 ID)
    public Page<Post> findPostByUserId(Long userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable);
    }

    // 4. 게시글 수정
    public boolean updatePost(Long id, String title, String contents, Integer likes) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        // 기존 데이터와 동일하면 업데이트 안 함
        Post updatedPost = post.toBuilder()
                .title(title)
                .contents(contents)
                .likes(likes)
                .build();

        // 수정된 새 Post 객체 생성
        postRepository.save(updatedPost);
        return true;
    }

    // 5. 게시글 삭제
    public boolean deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 게시물이 존재하지 않습니다.");
        }
        postRepository.deleteById(id);
        return true;
    }

    // 6. 좋아요 수 업데이트
    @Transactional
    public void updatePostLike(Long id, Integer likes) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        int updatedRows = postRepository.updatePostLike(id, likes, post.getVersion());

        if (updatedRows == 0) {
            // 업데이트 실패 시 낙관적 락 충돌로 간주
            throw new ObjectOptimisticLockingFailureException(Post.class, id);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updatePostInNewTransaction(Long id, String title, String contents, Integer likes) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        // in-place 업데이트: 직접 필드를 변경하면 Hibernate의 dirty checking이 작동하여 @Version이 자동 증가합니다.
        post.changeTitle(title);
        post.changeContents(contents);
        post.changeLikes(likes);
        postRepository.save(post);
        postRepository.flush();
    }

}

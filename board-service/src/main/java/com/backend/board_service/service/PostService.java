package com.backend.board_service.service;

import com.backend.board_service.dto.PostDTO;
import com.backend.board_service.entity.Post;
import com.backend.board_service.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 1. 게시글 작성
    public Long addPost(PostDTO dto) {
        PostDTO postDTO = new PostDTO(dto.getTitle(), dto.getContents(), dto.getUserID(), LocalDateTime.now(), dto.getLikes());
        Post post = Post.fromRegisterDTO(postDTO);
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
                        post.getUserID(),
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

        // 제목, 내용, 좋아요 수만 변경 가능 -> 나중에 좋아요 기능 따로 빼기
        postRepository.updatePost(id, title, contents, likes);
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

    // 6. 좋아요 수 업데이트 -> 아직 실제 사용 X
    public boolean updatePostLike(Long id, Integer likes) {
        Optional<Post> post = postRepository.findByPostId(id);
        if (post.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 게시물이 존재하지 않습니다.");
        }
        // 나중에 좋아요 증가, 감소 기능 등 따로 관리하기..
        postRepository.updatePostLike(id, likes);  // <- 좋아요만 업데이트
        return true;
    }

}

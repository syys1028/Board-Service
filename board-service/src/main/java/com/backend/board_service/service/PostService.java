package com.backend.board_service.service;

import com.backend.board_service.dto.PostDTO;
import com.backend.board_service.entity.Post;
import com.backend.board_service.entity.User;
import com.backend.board_service.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    // 1. 게시글 작성
    public Long addPost(PostDTO dto) {
        PostDTO postDTO = new PostDTO(dto.getTitle(), dto.getContents(), dto.getUserID(), dto.getCreatedAt(), dto.getLikes());
        Post post = Post.fromRegisterDTO(postDTO);
        return postRepository.savePost(post).getId();
    }

    // 2-1. 게시글 목록 조회
    public List<Post> findAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts;
    }

    // 2-2. 게시글 상세 조회 (작성자 검색)
    public Optional<PostDTO> findPostById(Long id) {
        return postRepository.findByUserId(id)
                .map(post -> new PostDTO(
                        post.getTitle(),
                        post.getContents(),
                        post.getUserID(),
                        post.getCreatedAt(),
                        post.getLikes()
                ));
    }

    // 2-3. 게시글 id로 회원 id 가져오기
    public Long getUserIdByPostId(Long id) {
        Optional<Post> post = postRepository.findByPostId(id);
        return post.map(Post::getUserID).orElse(null);
    }

    // 4. 게시글 수정
    public boolean updatePost(Long id, String title, String contents, Integer likes) {
        Optional<Post> existingPost = postRepository.findByPostId(id);
        if (existingPost.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 게시물이 존재하지 않습니다.");
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

    // 6. 좋아요 수 업데이트
    public boolean updatePostLike(Long id, Integer likes) {
        Optional<Post> post = postRepository.findByPostId(id);
        if (post.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 게시물이 존재하지 않습니다.");
        }

        // 좋아요 반영 방법 생각하기 (이렇게 3개 동시에 하거나, 아니면 레파지토리에 LikeCount 업데이트 함수 추가)
        postRepository.updatePost(id, post.get().getTitle(), post.get().getContents(), likes);
        return true;
    }
}

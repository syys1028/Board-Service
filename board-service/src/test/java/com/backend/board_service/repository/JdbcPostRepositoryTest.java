package com.backend.board_service.repository;

import com.backend.board_service.dto.AddressDTO;
import com.backend.board_service.dto.PostDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.entity.Post;
import com.backend.board_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JdbcPostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;
    private Long userId;

    @BeforeEach
    void setUp() {
        // given
        UserRegisterDTO testUserDTO = new UserRegisterDTO(
                "test@email.com", "password123", 25, Gender.MALE, LocalDateTime.now(),
                new AddressDTO("서울", "강남대로 1", "12345")
        );
        userId = userService.addUser(testUserDTO);
    }

    @Test
    void 게시글_저장_및_조회() {
        // given
        PostDTO postDTO = new PostDTO("테스트 제목", "테스트 내용", userId, LocalDateTime.now(), 0);

        // DTO -> Post 변환
        Post post = Post.fromRegisterDTO(postDTO);

        // when
        Post savedPost = postRepository.savePost(post);
        Optional<Post> foundPost = postRepository.findByPostId(savedPost.getId());

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getUserID()).isEqualTo(post.getUserID());
        assertThat(foundPost.get().getTitle()).isEqualTo(post.getTitle());
        assertThat(foundPost.get().getContents()).isEqualTo(post.getContents());
    }

    @Test
    void 게시글_사용자_상세조회() {
        // given
        PostDTO postDTO = new PostDTO("테스트 제목", "테스트 내용", userId, LocalDateTime.now(), 0);
        PostDTO postDTO2 = new PostDTO("테스트 제목", "테스트 내용", userId, LocalDateTime.now(), 0);

        // DTO -> Post 변환
        Post post = Post.fromRegisterDTO(postDTO);
        Post post2 = Post.fromRegisterDTO(postDTO2);

        // when
        postRepository.savePost(post);
        postRepository.savePost(post2);
        List<Post> foundPost = postRepository.findByUserId(userId);

        // then
        assertThat(foundPost.size()).isEqualTo(2);
    }

    @Test
    void 게시글_수정() {
        // given
        PostDTO postDTO = new PostDTO("테스트 제목", "테스트 내용", userId, LocalDateTime.now(), 0);
        Post post = Post.fromRegisterDTO(postDTO);
        Post savedPost = postRepository.savePost(post);

        // when
//        Post updatedPost = new Post(savedPost.getId(), "수정 테스트 제목", "수정 테스트 내용",
//                savedPost.getUserID(), savedPost.getCreatedAt(), savedPost.getLikes());
//
//        postRepository.updatePost(savedPost.getId(), updatedPost);
        String title = "수정 테스트 제목";
        String contents = "수정 테스트 내용";
        Integer likeCount = 1;
        postRepository.updatePost(savedPost.getId(), title, contents, likeCount);

        // then
        Optional<Post> foundPost = postRepository.findByPostId(savedPost.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("수정 테스트 제목");
        assertThat(foundPost.get().getContents()).isEqualTo("수정 테스트 내용");
        assertThat(foundPost.get().getLikes()).isEqualTo(likeCount);
    }

    @Test
    void 게시글_삭제() {
        // given
        PostDTO postDTO = new PostDTO("테스트 제목", "테스트 내용", userId, LocalDateTime.now(), 0);
        Post post = Post.fromRegisterDTO(postDTO);
        Post savedPost = postRepository.savePost(post);

        // when
        postRepository.deletePost(savedPost.getId());

        // then
        Optional<Post> foundPost = postRepository.findByPostId(savedPost.getId());
        assertThat(foundPost).isEmpty();
    }

    @Test
    void 좋아요_업데이트() {
        // given
        PostDTO postDTO = new PostDTO("테스트 제목", "테스트 내용", userId, LocalDateTime.now(), 0);
        Post post = Post.fromRegisterDTO(postDTO);
        Post savedPost = postRepository.savePost(post);

        // when
        Integer likeCount = 1;
        postRepository.updatePostLike(savedPost.getId(), likeCount);

        // then
        Optional<Post> foundPost = postRepository.findByPostId(savedPost.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getLikes()).isEqualTo(1);
    }
}
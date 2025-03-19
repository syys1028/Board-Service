package com.backend.board_service.repository;

import com.backend.board_service.dto.AddressDTO;
import com.backend.board_service.dto.PostDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.entity.Post;
import com.backend.board_service.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private User savedUser;

    @BeforeEach
    void setUp() {
        // given
        UserRegisterDTO testUserDTO = new UserRegisterDTO(
                "test@email.com", "password123", 25, Gender.MALE, LocalDateTime.now(),
                new AddressDTO("서울", "강남대로 1", "12345")
        );
        User user = User.fromRegisterDTO(testUserDTO);
        savedUser = userRepository.save(user);
    }

    @Test
    void 게시글_저장_및_조회() {
        // given
        Post post = Post.builder()
                .title("테스트 제목")
                .contents("테스트 내용")
                .user(savedUser)
                .createdAt(LocalDateTime.now())
                .likes(0)
                .build();

        // when
        Post savedPost = postRepository.save(post);
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getUser().getId()).isEqualTo(post.getUser().getId());
        assertThat(foundPost.get().getTitle()).isEqualTo(post.getTitle());
        assertThat(foundPost.get().getContents()).isEqualTo(post.getContents());
    }

    @Test
    void 게시글_사용자_상세조회() {
        // given
        postRepository.save(Post.builder().title("테스트1").contents("내용1").user(savedUser).createdAt(LocalDateTime.now()).likes(0).build());
        postRepository.save(Post.builder().title("테스트2").contents("내용2").user(savedUser).createdAt(LocalDateTime.now()).likes(0).build());

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Post> foundPosts = postRepository.findByUserId(savedUser.getId(), pageable);

        // then
        assertThat(foundPosts.getTotalElements()).isEqualTo(2);
    }

    @Test
    void 게시글_수정() {
        // given
        Post savedPost = postRepository.save(Post.builder()
                .title("테스트 제목")
                .contents("테스트 내용")
                .user(savedUser)
                .createdAt(LocalDateTime.now())
                .likes(0)
                .build());

        // when
        savedPost = savedPost.toBuilder().title("수정된 제목").contents("수정된 내용").likes(10).build();
        postRepository.save(savedPost);

        // then
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("수정된 제목");
        assertThat(foundPost.get().getContents()).isEqualTo("수정된 내용");
        assertThat(foundPost.get().getLikes()).isEqualTo(10);
    }

    @Test
    void 게시글_삭제() {
        // given
        Post savedPost = postRepository.save(Post.builder()
                .title("테스트 제목")
                .contents("테스트 내용")
                .user(savedUser)
                .createdAt(LocalDateTime.now())
                .likes(0)
                .build());

        // when
        postRepository.deleteById(savedPost.getId());

        // then
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());
        assertThat(foundPost).isEmpty();
    }

    @Test
    void 좋아요_업데이트() {
        // given
        Post savedPost = postRepository.save(Post.builder()
                .title("테스트 제목")
                .contents("테스트 내용")
                .user(savedUser)
                .createdAt(LocalDateTime.now())
                .likes(0)
                .build());

        em.flush();
        em.clear();

        Post postBeforeUpdate = postRepository.findById(savedPost.getId()).orElseThrow();
        Long currentVersion = postBeforeUpdate.getVersion();

        // when
        int updatedRows = postRepository.updatePostLike(savedPost.getId(), 5, currentVersion);
        em.flush();
        em.clear();

        // then
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());
        assertThat(updatedRows).isEqualTo(1);
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getLikes()).isEqualTo(5);
        assertThat(foundPost.get().getVersion()).isEqualTo(currentVersion + 1);
    }

    @Test
    void 게시글_검색_성공() {
        // given
        postRepository.save(Post.builder().title("Spring Boot Guide").contents("Spring 강의").user(savedUser).createdAt(LocalDateTime.now()).likes(0).build());
        postRepository.save(Post.builder().title("QueryDSL 튜토리얼").contents("QueryDSL 입문").user(savedUser).createdAt(LocalDateTime.now()).likes(0).build());

        // when
        List<Post> result = postRepository.searchPosts("Spring");

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("title").contains("Spring Boot Guide");
    }

}
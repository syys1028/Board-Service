package com.backend.board_service.repository;

import com.backend.board_service.dto.AddressDTO;
import com.backend.board_service.dto.PostDTO;
import com.backend.board_service.dto.UserDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.entity.Post;
import com.backend.board_service.entity.User;
import com.backend.board_service.service.UserService;
import jakarta.persistence.EntityManager;
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
class JPAPostRepositoryTest {

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
        savedUser = userRepository.saveUser(user);
    }

    @Test
    void 게시글_저장_및_조회() {
        // given
        PostDTO postDTO = new PostDTO("테스트 제목", "테스트 내용", savedUser.getId(), LocalDateTime.now(), 0);

        // DTO -> Post 변환
        Post post = Post.fromRegisterDTO(postDTO, savedUser);

        // when
        Post savedPost = postRepository.savePost(post);
        Optional<Post> foundPost = postRepository.findByPostId(savedPost.getId());

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getUser().getId()).isEqualTo(post.getUser().getId());
        assertThat(foundPost.get().getTitle()).isEqualTo(post.getTitle());
        assertThat(foundPost.get().getContents()).isEqualTo(post.getContents());
    }

    @Test
    void 게시글_사용자_상세조회() {
        // given
        PostDTO postDTO = new PostDTO("테스트 제목", "테스트 내용", savedUser.getId(), LocalDateTime.now(), 0);
        PostDTO postDTO2 = new PostDTO("테스트 제목", "테스트 내용", savedUser.getId(), LocalDateTime.now(), 0);

        // DTO -> Post 변환
        Post post = Post.fromRegisterDTO(postDTO, savedUser);
        Post post2 = Post.fromRegisterDTO(postDTO2, savedUser);

        // when
        postRepository.savePost(post);
        postRepository.savePost(post2);
        List<Post> foundPost = postRepository.findByUserId(savedUser.getId());

        // then
        assertThat(foundPost.size()).isEqualTo(2);
    }

    @Test
    void 게시글_수정() {
        // given
        PostDTO postDTO = new PostDTO("테스트 제목", "테스트 내용", savedUser.getId(), LocalDateTime.now(), 0);
        Post post = Post.fromRegisterDTO(postDTO, savedUser);
        Post savedPost = postRepository.savePost(post);

        // when
        String title = "수정 테스트 제목";
        String contents = "수정 테스트 내용";
        Integer likeCount = 1;
        PostDTO postDTO2 = new PostDTO(title, contents, savedUser.getId(), LocalDateTime.now(), likeCount);

        // DTO -> Post 변환
        Post repost = Post.fromRegisterDTO(postDTO2, savedUser);
        repost = repost.toBuilder().id(savedPost.getId()).build();
        postRepository.updatePost(savedPost.getId(), repost);

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
        PostDTO postDTO = new PostDTO("테스트 제목", "테스트 내용", savedUser.getId(), LocalDateTime.now(), 0);
        Post post = Post.fromRegisterDTO(postDTO, savedUser);
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
        PostDTO postDTO = new PostDTO("테스트 제목", "테스트 내용", savedUser.getId(), LocalDateTime.now(), 0);
        Post post = Post.fromRegisterDTO(postDTO, savedUser);
        Post savedPost = postRepository.savePost(post);

        // when
        Integer likeCount = 1;
        postRepository.updatePostLike(savedPost.getId(), likeCount);

        em.flush();
        em.clear();

        // then
        Optional<Post> foundPost = postRepository.findByPostId(savedPost.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getLikes()).isEqualTo(1);
    }

    @Test
    void 게시글_검색_성공() {
        // given
        PostDTO postDTO1 = new PostDTO("Spring Boot Tutorial", "Learn Spring Boot with examples", savedUser.getId(), LocalDateTime.now(), 0);
        PostDTO postDTO2 = new PostDTO("QueryDSL Example", "Learn QueryDSL basics", savedUser.getId(), LocalDateTime.now(), 0);
        PostDTO postDTO3 = new PostDTO("Hibernate Guide", "Deep dive into Hibernate", savedUser.getId(), LocalDateTime.now(), 0);

        Post post1 = Post.fromRegisterDTO(postDTO1, savedUser);
        Post post2 = Post.fromRegisterDTO(postDTO2, savedUser);
        Post post3 = Post.fromRegisterDTO(postDTO3, savedUser);

        postRepository.savePost(post1);
        postRepository.savePost(post2);
        postRepository.savePost(post3);

        // when
        List<Post> result = postRepository.searchPosts("Spring");

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("title").contains("Spring Boot Tutorial");
    }

}
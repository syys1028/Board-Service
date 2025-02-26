package com.backend.board_service.service;

import com.backend.board_service.dto.AddressDTO;
import com.backend.board_service.dto.PostDTO;
import com.backend.board_service.dto.UserDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.entity.Post;
import com.backend.board_service.repository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {
    @Autowired
    PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    PostRepository postRepository;

    private PostDTO postDTO;
    private Long userId;

    @BeforeEach
    void setUp() {
        // given
        UserRegisterDTO testUserDTO = new UserRegisterDTO(
                "test@email.com", "password123", 25, Gender.MALE, LocalDateTime.now(),
                new AddressDTO("서울", "강남대로 1", "12345")
        );
        userId = userService.addUser(testUserDTO);
        postDTO = new PostDTO("테스트 제목", "테스트 내용", userId, LocalDateTime.now(), 0);
    }

    @Test
    void 게시글_작성_성공() {
        // given & when
        postService.addPost(postDTO);
        Optional<PostDTO> foundPost = postService.findPostById(postDTO.getUserID());

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo(postDTO.getTitle());
        assertThat(foundPost.get().getUserID()).isEqualTo(postDTO.getUserID());
    }

    @Test
    void 게시글_목록_전체_조회_성공() {
        // given
        postService.addPost(postDTO);
        PostDTO postDTO1 = new PostDTO("테스트 제목2", "테스트 내용2", userId, LocalDateTime.now(), 0);
        postService.addPost(postDTO1);

        // when
        List<Post> result = postService.findAllPosts();

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 게시글_작성자_상세_조회_성공() {
        // given & when
        postService.addPost(postDTO);
        Optional<PostDTO> foundPost = postService.findPostById(postDTO.getUserID());

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo(postDTO.getTitle());
        assertThat(foundPost.get().getContents()).isEqualTo(postDTO.getContents());
        assertThat(foundPost.get().getUserID()).isEqualTo(postDTO.getUserID());
    }

    @Test
    void 게시글_수정_성공() {
        // given
        Long postId = postService.addPost(postDTO);
        String title = "수정 테스트 제목";
        String contents = "수정 테스트 내용";
        Integer likeCount = 1;

        // when
        boolean isUpdate = postService.updatePost(postId, title, contents, likeCount);
        Optional<PostDTO> foundPost = postService.findPostById(postDTO.getUserID());

        // then
        assertThat(isUpdate).isTrue();
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo(title);
        assertThat(foundPost.get().getContents()).isEqualTo(contents);
        assertThat(foundPost.get().getLikes()).isEqualTo(likeCount);
    }

    @Test
    void 게시글_삭제_성공() {
        // given
        Long postId = postService.addPost(postDTO);

        // when
        boolean isDelete = postService.deletePost(postId);
        Optional<PostDTO> foundPost = postService.findPostById(postDTO.getUserID());

        // then
        assertThat(isDelete).isTrue();
        assertThat(foundPost).isEmpty();
    }

    @Test
    void 게시글_좋아요_업데이트_성공() {
        // given
        Long postId = postService.addPost(postDTO);
        Integer likeCount = 2;

        // when
        boolean isUpdate = postService.updatePostLike(postId, likeCount);
        Optional<PostDTO> foundPost = postService.findPostById(postDTO.getUserID());

        // then
        assertThat(isUpdate).isTrue();
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getLikes()).isEqualTo(likeCount);
    }
}
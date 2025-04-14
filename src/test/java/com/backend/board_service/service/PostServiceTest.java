package com.backend.board_service.service;

import com.backend.board_service.entity.user.dto.AddressDTO;
import com.backend.board_service.entity.post.dto.PostDTO;
import com.backend.board_service.entity.user.dto.UserRegisterDTO;
import com.backend.board_service.entity.user.Gender;
import com.backend.board_service.entity.post.Post;
import com.backend.board_service.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PostServiceTest {
    @Autowired
    PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    private EntityManager em;

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
        Long postId = postService.addPost(postDTO);
        Optional<Post> foundPost = postRepository.findById(postId);

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo(postDTO.getTitle());
        assertThat(foundPost.get().getUser().getId()).isEqualTo(postDTO.getUserID());
    }

    @Test
    void 게시글_목록_전체_조회_성공() {
        // given
        postService.addPost(postDTO);
        PostDTO postDTO1 = new PostDTO("테스트 제목2", "테스트 내용2", userId, LocalDateTime.now(), 0);
        postService.addPost(postDTO1);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Post> result = postService.findAllPosts(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void 게시글_작성자_상세_조회_성공() {
        // given
        postService.addPost(postDTO);
        PostDTO postDTO1 = new PostDTO("테스트 제목2", "테스트 내용2", userId, LocalDateTime.now(), 0);
        postService.addPost(postDTO1);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Post> result = postService.findPostByUserId(userId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void 게시글_상세_조회_성공() {
        // given & when
        Long postId = postService.addPost(postDTO);
        Optional<Post> foundPost = postRepository.findById(postId);

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo(postDTO.getTitle());
        assertThat(foundPost.get().getContents()).isEqualTo(postDTO.getContents());
        assertThat(foundPost.get().getUser().getId()).isEqualTo(postDTO.getUserID());
    }

    @Test
    void 게시글_수정_성공() {
        // given
        Long postId = postService.addPost(postDTO);
        String title = "수정 테스트 제목";
        String contents = "수정 테스트 내용";
        Integer likeCount = 10;

        // when
        boolean isUpdate = postService.updatePost(postId, title, contents, likeCount);
        Optional<Post> foundPost = postRepository.findById(postId);

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
        Optional<PostDTO> foundPost = postService.findPostByPostId(postId);

        // then
        assertThat(isDelete).isTrue();
        assertThat(foundPost).isEmpty();
    }

    @Test
    void 게시글_좋아요_업데이트_성공() {
        // given
        Long postId = postService.addPost(postDTO);
        Post postBeforeUpdate = postRepository.findById(postId).orElseThrow();
        Long currentVersion = postBeforeUpdate.getVersion();

        // when
        postService.updatePostLike(postId, 5);
        em.flush();
        em.clear();

        // then
        Optional<Post> foundPost = postRepository.findById(postId);
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getLikes()).isEqualTo(5);
        assertThat(foundPost.get().getVersion()).isEqualTo(currentVersion + 1);
    }

    @Test
    void 낙관적_락_동시성_테스트() throws InterruptedException {
        // given: 게시글 생성
        Long postId = postService.addPost(postDTO);

        // 두 개의 스레드에서 동시에 업데이트 시도
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        Runnable task1 = () -> {
            try {
                postService.updatePostInNewTransaction(postId, "업데이트 제목 - Task1", "업데이트 내용 - Task1", 5);
            } catch (Exception e) {
                System.out.println("Task1 Exception: " + e);
                throw e;
            } finally {
                latch.countDown();
            }
        };

        Runnable task2 = () -> {
            try {
                postService.updatePostInNewTransaction(postId, "업데이트 제목 - Task2", "업데이트 내용 - Task2", 10);
            } catch (Exception e) {
                System.out.println("Task2 Exception: " + e);
                throw e;
            } finally {
                latch.countDown();
            }
        };

        executor.submit(task1);
        executor.submit(task2);

        latch.await();
        executor.shutdown();

        // 테스트 실행 결과
        // Task1 Exception: java.lang.IllegalArgumentException: 해당 게시글이 존재하지 않습니다.
        // Task2 Exception: java.lang.IllegalArgumentException: 해당 게시글이 존재하지 않습니다.
    }

}
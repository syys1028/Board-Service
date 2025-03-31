package com.backend.board_service.controller;

import com.backend.board_service.dto.post.PostDTO;
import com.backend.board_service.dto.post.PostUpdateDTO;
import com.backend.board_service.exception.PostNotFoundException;
import com.backend.board_service.service.PostService;
import com.backend.board_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    // 1. 게시글 작성 (POST /posts)
    @PostMapping
    public ResponseEntity<Long> savePost(@Valid @RequestBody PostDTO postDTO) {
        Long postId = postService.addPost(postDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    // 2. 게시글 목록 조회 (GET /posts)
    @GetMapping
    public ResponseEntity<Page<PostDTO>> getAllPosts(Pageable pageable) {
        Page<PostDTO> postDTOs = postService.findAllPosts(pageable).map(post ->
                new PostDTO(post.getTitle(), post.getContents(), post.getUser().getId(), post.getCreatedAt(), post.getLikes()));
        return ResponseEntity.ok(postDTOs);
    }

    // 3-1. 게시글 상세 조회 - PostId (GET /posts/{userId})
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable("id") Long postId) {
        PostDTO postDTO = postService.findPostByPostId(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시물이 존재하지 않습니다."));
        return ResponseEntity.ok(postDTO);
    }

    // 3-2. 게시글 상세 조회 - UserId (GET /posts/{userId})
    @GetMapping("/user/{email}")
    public ResponseEntity<Page<PostDTO>> getUserPosts(@PathVariable("email") String email, Pageable pageable) {
        Long userId = userService.getUserIdByEmail(email);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Page<PostDTO> postDTOs = postService.findPostByUserId(userId, pageable).map(post ->
                new PostDTO(post.getTitle(), post.getContents(), post.getUser().getId(), post.getCreatedAt(), post.getLikes()));

        return ResponseEntity.ok(postDTOs);
    }

    // 4. 게시글 수정 (PUT /posts/{id})
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable("id") Long postId, @Valid @RequestBody PostUpdateDTO postUpdateDTO) {
        postService.updatePost(postId, postUpdateDTO.getTitle(), postUpdateDTO.getContents(), postUpdateDTO.getLikes());
        return ResponseEntity.ok().build();
    }

    // 5. 게시글 삭제 (DELETE /posts/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    // 6. 게시글 좋아요 업데이트 (PATCH /posts/{id}/like)
    @PatchMapping("/{id}/like")
    public ResponseEntity<Void> updatePostLike(@PathVariable("id") Long postId, @RequestParam("likes") Integer likes) {
        postService.updatePostLike(postId, likes);
        return ResponseEntity.ok().build();
    }
}

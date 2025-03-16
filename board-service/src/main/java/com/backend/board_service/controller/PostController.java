package com.backend.board_service.controller;

import com.backend.board_service.dto.PostDTO;
import com.backend.board_service.dto.PostUpdateDTO;
import com.backend.board_service.exception.PostNotFoundException;
import com.backend.board_service.service.PostService;
import com.backend.board_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> postDTOs = postService.findAllPosts().stream()
                .map(post -> new PostDTO(post.getTitle(), post.getContents(), post.getUserID(), post.getCreatedAt(), post.getLikes()))
                .toList();
        return ResponseEntity.ok(postDTOs);
    }

    // 3-1. 게시글 상세 조회 - PostId (GET /posts/{userId})
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getUserPost(@PathVariable("id") Long postId) {
        PostDTO postDTO = postService.findPostByPostId(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시물이 존재하지 않습니다."));
        return ResponseEntity.ok(postDTO);
    }

    // 3-2. 게시글 상세 조회 - UserId (GET /posts/{userId})
    @GetMapping("/user/{email}")
    public ResponseEntity<List<PostDTO>> getUserPost(@PathVariable("email") String email) {
        Long userId = userService.getUserIdByEmail(email);
        if (userId == null) {
            throw new IllegalArgumentException("해당 이메일의 사용자가 존재하지 않습니다.");
        }

        List<PostDTO> postDTOs = postService.findPostByUserId(userId).stream()
                .map(post -> new PostDTO(post.getTitle(), post.getContents(), post.getUserID(), post.getCreatedAt(), post.getLikes()))
                .toList();

        if (postDTOs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 게시글 없을 때 204 No Content
        }

        return ResponseEntity.ok(postDTOs);
    }

    // 4. 게시글 수정 (PUT /posts/{id})
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable("id") Long postId, @Valid @RequestBody PostUpdateDTO postUpdateDTO) {
        boolean updated = postService.updatePost(postId, postUpdateDTO.getTitle(), postUpdateDTO.getContents(), postUpdateDTO.getLikes());
        if (updated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    // 5. 게시글 삭제 (DELETE /posts/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId) {
        boolean deleted = postService.deletePost(postId);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

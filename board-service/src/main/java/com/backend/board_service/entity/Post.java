package com.backend.board_service.entity;

import com.backend.board_service.dto.PostDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // 게시글 ID

    @Column(nullable = false)
    private String title;              // 제목

    @Column(nullable = false)
    private String contents;           // 내용

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                 // 작성자 ID (User 테이블의 ID)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;   // 작성 시간

    @Column(nullable = false)
    private Integer likes;         // 좋아요 수

    // DTO -> User로 변환
    public static Post fromRegisterDTO(PostDTO dto, User user) {
        return Post.builder()
                .title(dto.getTitle())
                .contents(dto.getContents())
                .user(user)
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
                .likes(dto.getLikes() != null ? dto.getLikes() : 0)
                .build();
    }

}

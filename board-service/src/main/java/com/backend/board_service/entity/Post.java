package com.backend.board_service.entity;

import com.backend.board_service.dto.PostDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Long id;                   // 게시글 ID
    private String title;              // 제목
    private String contents;           // 내용
    private Long userID;               // 작성자 ID (User 테이블의 ID)
    private LocalDateTime createdAt;   // 작성 시간
    private Integer likes;         // 좋아요 수

    // DTO -> User로 변환
    public static Post fromRegisterDTO(PostDTO dto) {
        return new Post(null, dto.getTitle(), dto.getContents(), dto.getUserID(), dto.getCreatedAt(), dto.getLikes());
    }

}

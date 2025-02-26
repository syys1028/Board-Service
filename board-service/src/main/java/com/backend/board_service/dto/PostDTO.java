package com.backend.board_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class PostDTO {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
    private String title;               // 제목

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 10, message = "내용은 최소 10자 이상 입력해주세요.")
    private String contents;            // 내용

    @NotNull
    private Long userID;                // 작성자 ID (User 테이블의 ID)
    private LocalDateTime createdAt;    // 작성 시간
    private Integer likes = 0;          // 좋아요 수

    public PostDTO(String title, String contents, Long userID, LocalDateTime createdAt, Integer likes) {
        this.title = title;
        this.contents = contents;
        this.userID = userID;
        this.createdAt = createdAt;
        this.likes = likes;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public Long getUserID() {
        return userID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getLikes() {
        return likes;
    }
}

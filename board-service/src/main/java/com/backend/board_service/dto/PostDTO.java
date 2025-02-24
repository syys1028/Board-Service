package com.backend.board_service.dto;

import java.time.LocalDateTime;

public class PostDTO {
    private String title;          // 제목
    private String contents;       // 내용
    private Long userID;           // 작성자 ID (User 테이블의 ID)
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성 시간
    private Integer likes = 0;     // 좋아요 수

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

package com.backend.board_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PostUpdateDTO {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String contents;

    @NotNull(message = "좋아요 수를 입력해주세요.")
    private Integer likes;

    public PostUpdateDTO(String title, String contents, Integer likes) {
        this.title = title;
        this.contents = contents;
        this.likes = likes;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public Integer getLikes() {
        return likes;
    }
}

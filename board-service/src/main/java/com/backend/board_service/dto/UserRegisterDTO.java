package com.backend.board_service.dto;

import com.backend.board_service.entity.Address;
import com.backend.board_service.entity.Gender;

import java.time.LocalDateTime;

// 회원가입용 DTO
public class UserRegisterDTO {
    private String email;                   // 사용자 이메일
    private String pw;                      // 사용자 비밀번호
    private int age;                        // 나이
    private Gender gender;                  // 성별
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성 시간
    private Long addressId;                // 주소

    public UserRegisterDTO(String email, String pw, int age, Gender gender, LocalDateTime createdAt, Long addressId) {
        this.email = email;
        this.pw = pw;
        this.age = age;
        this.gender = gender;
        this.createdAt = createdAt;
        this.addressId = addressId;
    }

    public String getEmail() {
        return email;
    }

    public String getPw() {
        return pw;
    }

    public int getAge() {
        return age;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Long getAddressId() {
        return addressId;
    }
}

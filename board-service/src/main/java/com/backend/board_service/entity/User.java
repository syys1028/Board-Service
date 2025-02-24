package com.backend.board_service.entity;

import java.time.LocalDateTime;

// Repository
public class User {
    private Long id;                        // 게시글 ID
    private String email;                   // 사용자 이메일
    private String pw;                      // 사용자 비밀번호
    private int age;                        // 나이
    private Gender gender;                  // 성별
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성 시간
    private Address address;                // 주소

    public User(Long id, String email, String pw, int age, Gender gender, LocalDateTime createdAt, Address address) {
        this.id = id;
        this.email = email;
        this.pw = pw;
        this.age = age;
        this.gender = gender;
        this.createdAt = createdAt;
        this.address = address;
    }

    public Long getId() {
        return id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Address getAddress() {
        return address;
    }
}

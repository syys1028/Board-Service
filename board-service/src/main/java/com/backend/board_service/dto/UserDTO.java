package com.backend.board_service.dto;

import com.backend.board_service.entity.Address;
import com.backend.board_service.entity.Gender;

import java.time.LocalDateTime;

// 코드 내에서 돌아다닐 정보
public class UserDTO {
    private String email;                   // 사용자 이메일
    private int age;                        // 나이
    private Gender gender;                  // 성별
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성 시간
    private Address address;                // 주소

    public UserDTO(String email, int age, Gender gender, LocalDateTime createdAt, Address address) {
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.createdAt = createdAt;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Address getAddress() {
        return address;
    }
}

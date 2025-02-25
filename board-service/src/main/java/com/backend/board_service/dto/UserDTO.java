package com.backend.board_service.dto;

import com.backend.board_service.entity.Gender;

import java.time.LocalDateTime;

// 코드 내에서 돌아다닐 정보
public class UserDTO {
    private String email;                   // 사용자 이메일
    private int age;                        // 나이
    private Gender gender;                  // 성별
    private LocalDateTime createdAt;        // 작성 시간
    private AddressDTO addressDTO;          // 주소

    public UserDTO(String email, int age, Gender gender, LocalDateTime createdAt, AddressDTO addressDTO) {
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.createdAt = createdAt;
        this.addressDTO = addressDTO;
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

    public AddressDTO getAddressDTO() {
        return addressDTO;
    }
}

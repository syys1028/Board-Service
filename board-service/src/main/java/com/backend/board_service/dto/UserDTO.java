package com.backend.board_service.dto;

import com.backend.board_service.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class UserDTO {
    private String email;                   // 사용자 이메일
    private int age;                        // 나이
    private Gender gender;                  // 성별
    private LocalDateTime createdAt;        // 작성 시간
    private AddressDTO addressDTO;          // 주소
}

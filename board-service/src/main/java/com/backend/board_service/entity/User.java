package com.backend.board_service.entity;

import com.backend.board_service.dto.UserRegisterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;                        // 게시글 ID
    private String email;                   // 사용자 이메일
    private String pw;                      // 사용자 비밀번호
    private int age;                        // 나이
    private Gender gender;                  // 성별
    private LocalDateTime createdAt;        // 작성 시간
    private Address address;                // 주소

    // DTO -> User로 변환
    public static User fromRegisterDTO(UserRegisterDTO dto) {
        return new User(null, dto.getEmail(), dto.getPw(), dto.getAge(), dto.getGender(), dto.getCreatedAt(), Address.fromDTO(dto.getAddressDTO()));
    }

    public User updateUserInfo(String pw, Integer age, Gender gender, Address address) {
        return this.toBuilder()   // toBuilder()가 생성되도록 toBuilder=true 옵션 필요
                .pw(pw != null ? pw : this.pw)
                .age(age != null ? age : this.age)
                .gender(gender != null ? gender : this.gender)
                .address(address != null ? address : this.address)
                .build();
    }
}

package com.backend.board_service.dto;

import com.backend.board_service.entity.Address;
import com.backend.board_service.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true)
public class UserRegisterDTO {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "올바른 이메일 형식이 아닙니다.")
    private String email;                   // 사용자 이메일

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String pw;                      // 사용자 비밀번호

    @Min(value = 0, message = "나이를 선택해주세요.")
    private int age;                        // 나이

    @NotNull(message = "성별을 선택해주세요.")
    private Gender gender;                  // 성별

    private LocalDateTime createdAt;        // 작성 시간

    @NotNull(message = "주소를 입력해주세요.")
    private AddressDTO addressDTO;          // 주소

}

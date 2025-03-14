package com.backend.board_service.dto;

import com.backend.board_service.entity.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateDTO {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String pw;                      // 사용자 비밀번호

    @Min(value = 0, message = "나이를 선택해주세요.")
    private int age;                        // 나이

    @NotNull(message = "성별을 선택해주세요.")
    private Gender gender;                  // 성별

    @NotNull(message = "주소를 입력해주세요.")
    private AddressDTO addressDTO;          // 주소
}

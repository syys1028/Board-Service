package com.backend.board_service.entity.user.dto;

import com.backend.board_service.entity.user.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class UserUpdateDTO {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String pw;                      // 사용자 비밀번호

    @Min(value = 0, message = "나이를 선택해주세요.")
    private Integer age;                        // 나이

    private Gender gender;                  // 성별

    private AddressDTO addressDTO;          // 주소
}

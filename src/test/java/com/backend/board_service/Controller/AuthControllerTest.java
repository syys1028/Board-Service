package com.backend.board_service.Controller;

import com.backend.board_service.controller.AuthController;
import com.backend.board_service.security.dto.JwtTokenDTO;
import com.backend.board_service.security.dto.LoginRequestDTO;
import com.backend.board_service.entity.user.Address;
import com.backend.board_service.entity.user.Gender;
import com.backend.board_service.entity.user.User;
import com.backend.board_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        // 테스트용 유저 저장
        userRepository.save(User.builder()
                .email("test@email.com")
                .pw(passwordEncoder.encode("1234"))  // 반드시 암호화!
                .age(20)
                .gender(Gender.MALE)
                .createdAt(LocalDateTime.now())
                .address(Address.builder().city("서울").street("강남대로").zipcode("12345").build())
                .build());
    }

    @Test
    void 로그인_성공_테스트() {
        // given
        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email("test@email.com")
                .pw("1234")
                .build();

        // when
        ResponseEntity<JwtTokenDTO> response = authController.login(loginRequest);
        JwtTokenDTO tokenDTO = response.getBody();

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(); // 200 OK 응답
        assertThat(tokenDTO).isNotNull();                                // 토큰이 null이 아님
        assertThat(tokenDTO.getAccessToken()).isNotBlank();              // Access 토큰 존재
        assertThat(tokenDTO.getRefreshToken()).isNotBlank();             // Refresh 토큰 존재
    }

}

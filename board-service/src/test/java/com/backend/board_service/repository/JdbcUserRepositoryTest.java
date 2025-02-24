package com.backend.board_service.repository;

import com.backend.board_service.dto.AddressDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.entity.Address;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JdbcUserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 회원_저장_및_조회() {
        // Given
        UserRegisterDTO userDTO = new UserRegisterDTO(
                "test@email.com", "password123", 25, Gender.MALE, LocalDateTime.now(),
                new AddressDTO("군산", "나운우회로 91", "54124")
        );

        // DTO → User 변환
        User user = User.fromRegisterDTO(userDTO);

        // When
        User savedUser = userRepository.saveUser(user);
        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());

        // Then
        assertThat(foundUser).isPresent();  // 존재 확인
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail()); // 이메일
        assertThat(foundUser.get().getAge()).isEqualTo(user.getAge()); // 나이
        assertThat(foundUser.get().getAddress().getCity()).isEqualTo(user.getAddress().getCity()); // 주소의 도시
    }

    @Test
    void 수정() {

    }

    @Test
    void 삭제() {

    }

    @Test
    void 이메일_중복_방지() {

    }

}
package com.backend.board_service.repository;

import com.backend.board_service.dto.user.AddressDTO;
import com.backend.board_service.dto.user.UserRegisterDTO;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 회원_저장_및_조회() {
        // given
        UserRegisterDTO userDTO = new UserRegisterDTO(
                "aaa123@naver.com", "asdf1234!", 25, Gender.FEMALE, LocalDateTime.now(),
                new AddressDTO("군산", "나운우회로 91", "54124")
        );

        // DTO → User 변환
        User user = User.fromRegisterDTO(userDTO);

        // when
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());

        // then
        assertThat(foundUser).isPresent();  // 존재 확인
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail()); // 이메일
        assertThat(foundUser.get().getAge()).isEqualTo(user.getAge()); // 나이
        assertThat(foundUser.get().getAddress().getCity()).isEqualTo(user.getAddress().getCity()); // 주소의 도시
    }

    @Test
    void 회원_수정() {
        // given
        UserRegisterDTO userDTO = new UserRegisterDTO(
                "aaa123@naver.com", "asdf1234!", 25, Gender.FEMALE, LocalDateTime.now(),
                new AddressDTO("군산", "나운우회로 91", "54124")
        );
        User user = User.fromRegisterDTO(userDTO);
        User savedUser = userRepository.save(user);

        // when
        User updatedUser = savedUser.toBuilder()
                .age(30)
                .build();
        userRepository.save(updatedUser);

        // then
        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getAge()).isEqualTo(30);
    }

    @Test
    void 회원_삭제() {
        // given
        UserRegisterDTO userDTO = new UserRegisterDTO(
                "aaa123@naver.com", "asdf1234!", 25, Gender.FEMALE, LocalDateTime.now(),
                new AddressDTO("군산", "나운우회로 91", "54124")
        );
        User user = User.fromRegisterDTO(userDTO);
        User savedUser = userRepository.save(user);

        // when
        userRepository.deleteById(savedUser.getId());

        // then
        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());
        assertThat(foundUser).isEmpty();
    }

}
package com.backend.board_service.service;

import com.backend.board_service.dto.AddressDTO;
import com.backend.board_service.dto.UserDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.dto.UserUpdateDTO;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserRegisterDTO testUserDTO;

    @BeforeEach
    void setUp() {
        // given
        testUserDTO = new UserRegisterDTO(
                "aaa123@naver.com", "asdf1234!", 25, Gender.FEMALE, LocalDateTime.now(),
                new AddressDTO("군산", "나운우회로 91", "54124")
        );
    }

    @Test
    void 이메일_중복_방지() {
        // given
        userService.addUser(testUserDTO);

        // when & then
        assertThrows(IllegalStateException.class, () -> userService.addUser(testUserDTO));
    }

    @Test
    void 회원_가입_및_조회_성공() {
        // given & when
        userService.addUser(testUserDTO);
        Optional<UserDTO> foundUser = userService.findUserByEmail(testUserDTO.getEmail());

        // then
        assertThat(foundUser).isPresent();  // 존재 확인
        assertThat(foundUser.get().getEmail()).isEqualTo(testUserDTO.getEmail()); // 이메일
        assertThat(foundUser.get().getAge()).isEqualTo(testUserDTO.getAge()); // 나이
        assertThat(foundUser.get().getAddressDTO().getCity()).isEqualTo(testUserDTO.getAddressDTO().getCity()); // 주소의 도시
    }

    @Test
    void 회원_수정_성공() {
        // given
        Long userId = userService.addUser(testUserDTO);
        UserUpdateDTO updatedUserDTO = new UserUpdateDTO(
                "asdf1234!", null, Gender.FEMALE,
                new AddressDTO("서울", "강남대로 1", "12345")
        );

        // when
        boolean isUpdate = userService.updateUser(testUserDTO.getEmail(), updatedUserDTO);
        Optional<UserDTO> foundUser = userService.findUserByEmail(testUserDTO.getEmail());

        // then
        assertThat(isUpdate).isTrue();
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(testUserDTO.getEmail());
        assertThat(foundUser.get().getAge()).isEqualTo(25);
        assertThat(foundUser.get().getAddressDTO().getCity()).isEqualTo(updatedUserDTO.getAddressDTO().getCity());
    }

    @Test
    void 회원_삭제_성공() {
        // given
        userService.addUser(testUserDTO);

        // when
        boolean isDelete = userService.deleteUser(testUserDTO.getEmail());
        Optional<UserDTO> foundUser = userService.findUserByEmail(testUserDTO.getEmail());

        // then
        assertThat(isDelete).isTrue();
        assertThat(foundUser).isEmpty();
    }

    @Test
    void 비밀번호_암호화_테스트() {
        // given
        Long userId = userService.addUser(testUserDTO);

        // when
        Optional<UserDTO> foundUser = userService.findUserByEmail(testUserDTO.getEmail());

        // then
        assertThat(foundUser).isPresent();
        String encryptedPassword = userRepository.findByEmail(testUserDTO.getEmail()).get().getPw();

        assertThat(encryptedPassword).isNotEqualTo(testUserDTO.getPw()); // 원래 비밀번호와 다름
        assertThat(passwordEncoder.matches(testUserDTO.getPw(), encryptedPassword)).isTrue(); // matches()로 검증
    }
}
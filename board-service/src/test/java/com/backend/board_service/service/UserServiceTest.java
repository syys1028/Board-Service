package com.backend.board_service.service;

import com.backend.board_service.dto.AddressDTO;
import com.backend.board_service.dto.UserDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

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

//    @Test
//    void 회원_수정_성공() {
//        // given
//        Long userId = userService.addUser(testUserDTO);
//        UserRegisterDTO updatedUserDTO = new UserRegisterDTO(
//                "aaa123@naver.com", "asdf1234!", 30, Gender.FEMALE, LocalDateTime.now(),
//                new AddressDTO("서울", "강남대로 1", "12345")
//        );
//
//        // when
//        boolean isUpdate = userService.updateUser(userId, updatedUserDTO);
//        Optional<UserDTO> foundUser = userService.findUserById(userId);
//
//        // then
//        assertThat(isUpdate).isTrue();
//        assertThat(foundUser).isPresent();
//        assertThat(foundUser.get().getEmail()).isEqualTo(updatedUserDTO.getEmail());
//        assertThat(foundUser.get().getAge()).isEqualTo(updatedUserDTO.getAge());
//        assertThat(foundUser.get().getAddressDTO().getCity()).isEqualTo(updatedUserDTO.getAddressDTO().getCity());
//    }

    @Test
    void 회원_삭제_성공() {
        // given
        Long userId = userService.addUser(testUserDTO);

        // when
        boolean isDelete = userService.deleteUser(userId);
        Optional<UserDTO> foundUser = userService.findUserByEmail(testUserDTO.getEmail());

        // then
        assertThat(isDelete).isTrue();
        assertThat(foundUser).isEmpty();
    }
}
package com.backend.board_service.service;

import com.backend.board_service.dto.UserDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.entity.User;
import com.backend.board_service.repository.JdbcUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final JdbcUserRepository jdbcUserRepository;

    public UserService(JdbcUserRepository jdbcUserRepository) {
        this.jdbcUserRepository = jdbcUserRepository;
    }

    // 1. 회원 가입
    public Long addUser(UserRegisterDTO dto) {
        User user = new User(null, dto.getEmail(), dto.getPw(), dto.getAge(), dto.getGender(), null, dto.getAddress());
        return jdbcUserRepository.saveUser(user).getId();
    }

    // 2. 회원 정보 조회
    public Optional<User> findUserByEmail(String email) {
        return jdbcUserRepository.findByEmail(email);
    }

    // 3. 회원 정보 수정
    public boolean updateUser(Long id, UserDTO userDTO) {
        //
        // userRepository.updateUser(id, user);
        return true;
    }

    // 4. 회원 삭제
    public boolean deleteUser(Long id) {
        jdbcUserRepository.deleteUser(id);
        return true;
    }

}

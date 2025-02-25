package com.backend.board_service.service;

import com.backend.board_service.dto.AddressDTO;
import com.backend.board_service.dto.UserDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.entity.Address;
import com.backend.board_service.entity.User;
import com.backend.board_service.repository.JdbcUserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final JdbcUserRepository jdbcUserRepository;

    public UserService(JdbcUserRepository jdbcUserRepository) {
        this.jdbcUserRepository = jdbcUserRepository;
    }

    // 0. 이메일 중복 확인
    private void validateDuplicateEmail(String email) {
        jdbcUserRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 등록된 이메일입니다.");
                });
    }

    // 1. 회원 가입
    public Long addUser(UserRegisterDTO dto) {
        validateDuplicateEmail(dto.getEmail());

        UserRegisterDTO userDTO = new UserRegisterDTO(dto.getEmail(), dto.getPw(), dto.getAge(), dto.getGender(), LocalDateTime.now(), dto.getAddressDTO());
        User user = User.fromRegisterDTO(userDTO);
        return jdbcUserRepository.saveUser(user).getId();
    }

    // 2. 회원 정보 조회
    public Optional<UserDTO> findUserByEmail(String email) {
        return jdbcUserRepository.findByEmail(email)
                .map(user -> new UserDTO(
                        user.getEmail(),
                        user.getAge(),
                        user.getGender(),
                        user.getCreatedAt(),
                        AddressDTO.fromEntity(user.getAddress())
                ));
    }

    public Optional<UserDTO> findUserById(Long id) {
        return jdbcUserRepository.findById(id)
                .map(user -> new UserDTO(
                        user.getEmail(),
                        user.getAge(),
                        user.getGender(),
                        user.getCreatedAt(),
                        AddressDTO.fromEntity(user.getAddress())
                ));
    }

    // 3. 회원 정보 수정
    public boolean updateUser(Long id, UserRegisterDTO userDTO) {
        Optional<User> existingUser = jdbcUserRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다.");
        }

        // 나중에 변경된 값만 반영하도록 수정, 그리고 어떤 값을 수정할 수 있도록 할건지 고려 (이메일 x)

        LocalDateTime createdAt = existingUser.get().getCreatedAt(); // 기존 회원가입 시간 유지 (수정에 반영 x)
        Address existingAddress = existingUser.get().getAddress(); // 기존 주소 유지

        User updatedUser = new User(
                id, userDTO.getEmail(), userDTO.getPw(),
                userDTO.getAge(), userDTO.getGender(), createdAt,
                new Address(existingAddress.getAddress_id(),
                        userDTO.getAddressDTO().getCity(),
                        userDTO.getAddressDTO().getStreet(),
                        userDTO.getAddressDTO().getZipcode())
        );

        jdbcUserRepository.updateUser(id, updatedUser);
        return true;
    }

    // 4. 회원 삭제
    public boolean deleteUser(Long id) {
        Optional<User> user = jdbcUserRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다.");
        }
        jdbcUserRepository.deleteUser(id);
        return true;
    }
}

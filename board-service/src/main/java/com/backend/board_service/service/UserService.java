package com.backend.board_service.service;

import com.backend.board_service.dto.user.AddressDTO;
import com.backend.board_service.dto.user.UserDTO;
import com.backend.board_service.dto.user.UserRegisterDTO;
import com.backend.board_service.dto.user.UserUpdateDTO;
import com.backend.board_service.entity.Address;
import com.backend.board_service.entity.User;
import com.backend.board_service.exception.NoChangesException;
import com.backend.board_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 0. 이메일 중복 확인
    private void validateDuplicateEmail(String email) {
        userRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 등록된 이메일입니다.");
                });
    }

    // 1. 회원 가입
    public Long addUser(UserRegisterDTO dto) {
        validateDuplicateEmail(dto.getEmail());

        String encodedPw = passwordEncoder.encode(dto.getPw());

        User user = User.fromRegisterDTO(dto.toBuilder().pw(encodedPw).build());
        return userRepository.save(user).getId();
    }

    // 2-1. 회원 정보 조회 (이메일)
    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepository.findByAddress(email)
                .map(user -> new UserDTO(
                        user.getEmail(),
                        user.getAge(),
                        user.getGender(),
                        user.getCreatedAt(),
                        AddressDTO.fromEntity(user.getAddress())
                ));
    }

    // 2-3. 회원 정보 조회 후 아이디 반환 (이메일)
    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email).map(User::getId).orElse(null);
    }

    // 3. 회원 정보 수정
    public boolean updateUser(String email, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 존재하지 않습니다."));

        // 주소 업데이트: 새로운 주소가 주어진 경우에만 변경 (기존 주소를 기본값으로 사용)
        Address updatedAddress = user.getAddress();
        if (userUpdateDTO.getAddressDTO() != null) {
            updatedAddress = Address.fromDTO(userUpdateDTO.getAddressDTO());
        }

        // 변경된 값만 반영하여 새 User 객체 생성 (setter 없이, toBuilder() 활용)
        User updatedUser = user.toBuilder()
                .pw(userUpdateDTO.getPw() != null ? passwordEncoder.encode(userUpdateDTO.getPw()) : user.getPw())
                .age(userUpdateDTO.getAge() != null ? userUpdateDTO.getAge() : user.getAge())
                .gender(userUpdateDTO.getGender() != null ? userUpdateDTO.getGender() : user.getGender())
                .address(updatedAddress)
                .build();

        if (user.equals(updatedUser)) {
            throw new NoChangesException("변경된 사항이 없습니다.");
        }

        userRepository.save(updatedUser);
        return true;
    }

    // 4. 회원 삭제
    public boolean deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 존재하지 않습니다."));

        userRepository.delete(user);
        return true;
    }

}

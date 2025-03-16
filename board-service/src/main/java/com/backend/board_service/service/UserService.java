package com.backend.board_service.service;

import com.backend.board_service.dto.AddressDTO;
import com.backend.board_service.dto.UserDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.dto.UserUpdateDTO;
import com.backend.board_service.entity.Address;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.entity.User;
import com.backend.board_service.exception.NoChangesException;
import com.backend.board_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 0. 이메일 중복 확인
    private void validateDuplicateEmail(String email) {
        userRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 등록된 이메일입니다.");
                });
    }

    // 0. 아이디 확인
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    // 1. 회원 가입
    public Long addUser(UserRegisterDTO dto) {
        validateDuplicateEmail(dto.getEmail());

        UserRegisterDTO userDTO = new UserRegisterDTO(dto.getEmail(), dto.getPw(), dto.getAge(), dto.getGender(), LocalDateTime.now(), dto.getAddressDTO());
        User user = User.fromRegisterDTO(userDTO);
        return userRepository.saveUser(user).getId();
    }

    // 2-1. 회원 정보 조회 (이메일)
    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserDTO(
                        user.getEmail(),
                        user.getAge(),
                        user.getGender(),
                        user.getCreatedAt(),
                        AddressDTO.fromEntity(user.getAddress())
                ));
    }

    // 2-2. 회원 정보 조회 (아이디)
    public Optional<UserDTO> findUserById(Long id) {
        return userRepository.findById(id)
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
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(User::getId).orElse(null);
    }

    // 3. 회원 정보 수정
    public boolean updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다.");
        }

        User user = existingUser.get();

        // 주소 업데이트: 새로운 주소가 주어진 경우에만 변경 (기존 주소를 기본값으로 사용)
        Address updatedAddress = user.getAddress();
        if (userUpdateDTO.getAddressDTO() != null) {
            AddressDTO addressDTO = userUpdateDTO.getAddressDTO();
            Address existingAddress = user.getAddress();
            if (!addressDTO.getCity().equals(existingAddress.getCity()) ||
                    !addressDTO.getStreet().equals(existingAddress.getStreet()) ||
                    !addressDTO.getZipcode().equals(existingAddress.getZipcode())) {

                updatedAddress = Address.builder()
                        .address_id(existingAddress.getAddress_id())
                        .city(addressDTO.getCity())
                        .street(addressDTO.getStreet())
                        .zipcode(addressDTO.getZipcode())
                        .build();
            }
        }

        // 변경된 값만 반영하여 새 User 객체 생성 (setter 없이, toBuilder() 활용)
        User updatedUser = user.toBuilder()
                .pw(userUpdateDTO.getPw() != null ? userUpdateDTO.getPw() : user.getPw())
                .age(userUpdateDTO.getAge() != null ? userUpdateDTO.getAge() : user.getAge())
                .gender(userUpdateDTO.getGender() != null ? userUpdateDTO.getGender() : user.getGender())
                .address(updatedAddress)
                .build();


        if (user.equals(updatedUser)) {
            throw new NoChangesException("변경된 사항이 없습니다.");
        }

        userRepository.updateUser(id, updatedUser);
        return true;
    }

    // 4. 회원 삭제
    public boolean deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다.");
        }
        userRepository.deleteUser(id);
        return true;
    }

}

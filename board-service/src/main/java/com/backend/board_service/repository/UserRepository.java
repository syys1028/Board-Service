package com.backend.board_service.repository;

import com.backend.board_service.entity.User;

import java.util.Optional;

public interface UserRepository {
    User saveUser(User user);                       // 1. 회원 가입
    Optional<User> findByEmail(String email);       // 2. 회원 정보 조회
    void updateUser(Long id, User user);            // 3. 회원 정보 수정 -> Service? DTO 사용?
    void deleteUser(Long id);                       // 4. 회원 삭제
}

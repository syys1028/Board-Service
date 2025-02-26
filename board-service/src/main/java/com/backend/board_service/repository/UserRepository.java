package com.backend.board_service.repository;

import com.backend.board_service.entity.User;

import java.util.Optional;

public interface UserRepository {
    User saveUser(User user);                       // 1. 회원 가입
    Optional<User> findById(Long id);               // 2-1. 회원 정보 조회 (아이디)
    Optional<User> findByEmail(String email);       // 2-2. 회원 정보 조회 (이메일)
    void updateUser(Long id, User user);            // 3. 회원 정보 수정
    void deleteUser(Long id);                       // 4. 회원 삭제
}

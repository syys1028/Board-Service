package com.backend.board_service.controller;

import com.backend.board_service.dto.JwtTokenDTO;
import com.backend.board_service.dto.LoginRequestDTO;
import com.backend.board_service.entity.User;
import com.backend.board_service.jwt.JwtTokenProvider;
import com.backend.board_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<JwtTokenDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(loginRequest.getPw(), user.getPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 직접 UserDetails 객체를 만들어서 넘기기
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, new ArrayList<>()
        );

        JwtTokenDTO tokenDTO = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(tokenDTO);
    }

}

package com.backend.board_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class JwtTokenDTO {
    private String grantType;       // JWT에 대한 인증 타입
    private String accessToken;
    private String refreshToken;
}

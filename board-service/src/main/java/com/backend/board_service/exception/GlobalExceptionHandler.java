package com.backend.board_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 회원을 찾을 수 없는 경우 (IllegalArgumentException 처리)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleNotFoundException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // 2. 이메일 중복 예외 (IllegalStateException 처리)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleDuplicateEmailException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // 3. 기타 예외 처리 (예상치 못한 에러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
    }

}

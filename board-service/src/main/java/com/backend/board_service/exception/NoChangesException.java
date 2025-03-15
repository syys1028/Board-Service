package com.backend.board_service.exception;

public class NoChangesException extends RuntimeException {
    public NoChangesException(String message) {
        super(message);
    }
}

package com.example.secondhand.config;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final int code;

    public AuthException(String message) {
        super(message);
        this.code = 401;
    }

    public AuthException(int code, String message) {
        super(message);
        this.code = code;
    }
}
package com.smile.testcases.auth;

import lombok.Getter;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

@Getter
public enum ErrorMessages {
    USER_DISABLED(HTTP_UNAUTHORIZED, "user account is abnormal", "User is disabled"),
    USERNAME_PASSWORD_INCORRECT(HTTP_UNAUTHORIZED, "username or password is incorrect", "Bad credentials");
    private final int statusCode;
    private final String message;
    private final Object data;

    ErrorMessages(int statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
}

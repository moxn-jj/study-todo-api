package com.mun.todo.enums;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum CustomErrorCode {

    ERR_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "S0001", "권한이 없습니다.");

    private HttpStatus status;
    private String code;
    private String message;

    CustomErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
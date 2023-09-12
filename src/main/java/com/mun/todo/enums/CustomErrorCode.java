package com.mun.todo.enums;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum CustomErrorCode {

    ERR_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "S0001", "권한이 없습니다.")
    , ERR_DUPLICATION_EMAIL(HttpStatus.BAD_REQUEST, "S0002", "이미 존재하는 이메일 입니다.")
    , ERR_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "S0003", "Refresh Token이 유효하지 않습니다.")
    , ERR_ALREADY_LOGOUT(HttpStatus.UNAUTHORIZED, "S0004", "로그아웃 된 사용자입니다.")
    , ERR_INVALID_TOKEN_USER(HttpStatus.UNAUTHORIZED, "S0005", "토큰의 유저 정보가 일치하지 않습니다.")
    , ERR_UNAUTHORIZED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "S0006", "권한 정보가 없는 토큰입니다.")
    ;

    private HttpStatus status;
    private String code;
    private String message;

    CustomErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

package com.mun.todo.enums;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum CustomErrorCode {

    ERR_UNKNOWN(HttpStatus.UNAUTHORIZED, "E0000", "정의되지 않은 에러입니다.")
    , ERR_BAD_REQUEST(HttpStatus.BAD_REQUEST, "E0001", "잘못된 요청입니다.")
    , ERR_ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "E0002", "권한이 없습니다.")
    , ERR_BAD_CREDENTIALS(HttpStatus.BAD_REQUEST, "E0003", "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.")
    , ERR_INTERNAL_AUTHENTICATION_SERVICE(HttpStatus.BAD_REQUEST, "E0004", "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.")
    , ERR_USERNAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "E0005", "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.")
    , ERR_AUTHENTICATION_CREDENTIALS_NOT_FOUND(HttpStatus.BAD_REQUEST, "E0006", "인증 요청이 거부되었습니다. 관리자에게 문의하세요.")

    , ERR_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "M0001", "로그인이 필요합니다.")
    , ERR_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "M0002", "Refresh Token이 유효하지 않습니다.")
    , ERR_INVALID_TOKEN_USER(HttpStatus.UNAUTHORIZED, "M0003", "토큰의 유저 정보가 일치하지 않습니다.")
    , ERR_UNAUTHORIZED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "M0004", "권한 정보가 없는 토큰입니다.")
    , ERR_WRONG_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "M0005", "잘못된 JWT 서명입니다.")
    , ERR_UNSUPPORTED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "M0006", "지원되지 않는 JWT 토큰입니다.")
    , ERR_ILLEGAL_ARGUMENT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "M0007", "JWT 토큰이 잘못되었습니다.")
    , ERR_SIGNATURE_TOKEN(HttpStatus.UNAUTHORIZED, "M0008", "JWT 토큰 서명이 올바르지 않습니다.")
    , ERR_ALREADY_LOGOUT(HttpStatus.UNAUTHORIZED, "M0012", "로그아웃 된 사용자입니다.")
    , ERR_DUPLICATION_EMAIL(HttpStatus.BAD_REQUEST, "M0013", "이미 존재하는 이메일 입니다.")
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

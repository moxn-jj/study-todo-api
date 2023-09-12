package com.mun.todo.exception;

import com.mun.todo.controller.dto.ErrorCodeDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({CustomException.class})
    public ResponseEntity<ErrorCodeDto> exceptionHandler(CustomException e) {

        return ResponseEntity
                .status(e.getError().getStatus())
                .body(ErrorCodeDto.builder()
                        .errorCode(e.getError().getCode())
                        .errorMessage(e.getError().getMessage())
                        .build());
    }


    @org.springframework.web.bind.annotation.ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrorCodeDto> authenticationExceptionHandler(AuthenticationException e) {

        String errorMessage = "";

        if (e instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
        } else if (e instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
        } else if (e instanceof UsernameNotFoundException) {
            errorMessage = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
        } else if (e instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
        } else {
            errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorCodeDto.builder()
                        .errorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                        .errorMessage(errorMessage)
                        .build());
    }
}

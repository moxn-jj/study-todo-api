package com.mun.todo.exception;

import com.mun.todo.controller.dto.ErrorCodeDto;
import com.mun.todo.enums.CustomErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({CustomException.class})
    public ResponseEntity<ErrorCodeDto> customExceptionHandler(CustomException e) {

        log.info(e.getMessage());

        return ResponseEntity
                .status(e.getError().getStatus())
                .body(ErrorCodeDto.builder()
                        .code(e.getError().getCode())
                        .message(e.getError().getMessage())
                        .build());
    }


    @org.springframework.web.bind.annotation.ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrorCodeDto> authenticationExceptionHandler(AuthenticationException e) {

        log.info(e.getMessage());

        CustomErrorCode errorType = CustomErrorCode.ERR_UNKNOWN;

        if (e instanceof BadCredentialsException) {
            errorType = CustomErrorCode.ERR_BAD_CREDENTIALS;
        }else if (e instanceof InternalAuthenticationServiceException) {
            errorType = CustomErrorCode.ERR_INTERNAL_AUTHENTICATION_SERVICE;
        }else if (e instanceof UsernameNotFoundException) {
            errorType = CustomErrorCode.ERR_USERNAME_NOT_FOUND;
        }else if (e instanceof AuthenticationCredentialsNotFoundException) {
            errorType = CustomErrorCode.ERR_AUTHENTICATION_CREDENTIALS_NOT_FOUND;
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorCodeDto.builder()
                        .code(errorType.getCode())
                        .message(errorType.getMessage())
                        .build());
    }
}

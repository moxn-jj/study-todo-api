package com.mun.todo.exception;

import com.mun.todo.controller.dto.ErrorCodeDto;
import org.springframework.http.ResponseEntity;
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

}

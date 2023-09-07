package com.mun.todo.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorCodeDto {
    private String errorCode;
    private String errorMessage;

    @Builder
    public ErrorCodeDto(String errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}

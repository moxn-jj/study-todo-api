package com.mun.todo.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorCodeDto {

    private String code;
    private String message;

    @Builder
    public ErrorCodeDto(String code, String message){
        this.code = code;
        this.message = message;
    }
}

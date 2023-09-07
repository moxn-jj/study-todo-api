package com.mun.todo.exception;

import com.mun.todo.enums.CustomErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private CustomErrorCode error;

    public CustomException(CustomErrorCode e) {
        super(e.getMessage());
        this.error = e;
    }
}
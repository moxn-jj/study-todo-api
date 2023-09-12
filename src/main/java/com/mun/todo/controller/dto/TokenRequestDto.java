package com.mun.todo.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 재발급을 위한 토큰 정보
 */
@Getter
@NoArgsConstructor
public class TokenRequestDto {

    private String accessToken;
    private String refreshToken;
}
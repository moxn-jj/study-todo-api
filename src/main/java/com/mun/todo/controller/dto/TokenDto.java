package com.mun.todo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {

    private String grantType;
    private String encryptoAccessToken;
    private String encryptoRefreshToken;
    private Long accessTokenExpiredsIn;

    /**
     * RefreshToken을 HTTP_ONLY로 설정하여 cookie에 담기 때문에
     * 사용자에게는 원본 값을 보내지 않음
     *
     * @return refreshToken에 "HTTP_ONLY"를 담아서 보냄
     */
    public String resetRefreshTokenForSecure() {
        return this.encryptoRefreshToken = "HTTP_ONLY";
    }
}

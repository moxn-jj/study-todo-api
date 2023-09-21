package com.mun.todo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityContext에 담긴 유저 정보를 제공
 */
@Slf4j
public class SecurityUtil {

    /**
     * 현재 로그인 중인 내 정보 가져오기 (from SecurityContext)
     * @return Long : 현재 로그인 중인 id
     */
    public static Long getCurrentMemberId() {

        // JwtFilter에서 저장한 유저 정보를 가져옴
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }
}

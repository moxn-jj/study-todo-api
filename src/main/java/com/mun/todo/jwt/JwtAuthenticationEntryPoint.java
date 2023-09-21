package com.mun.todo.jwt;

import com.mun.todo.enums.CustomErrorCode;
import com.mun.todo.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증 정보 없을 때 (401) 에러 커스텀
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ExceptionUtil exceptionUtil;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {

        exceptionUtil.sendErrorResponse(response, CustomErrorCode.ERR_AUTHENTICATION);
    }
}

package com.mun.todo.jwt;

import com.mun.todo.enums.CustomErrorCode;
import com.mun.todo.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 접근 권한 없을 때(403) 에러 커스텀
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ExceptionUtil exceptionUtil;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        exceptionUtil.sendErrorResponse(response, CustomErrorCode.ERR_ACCESS_DENIED);
    }
}

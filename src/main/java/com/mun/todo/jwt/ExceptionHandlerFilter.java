package com.mun.todo.jwt;

import com.mun.todo.enums.CustomErrorCode;
import com.mun.todo.exception.CustomException;
import com.mun.todo.util.ExceptionUtil;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT Filter 앞에 들어 갈 Exception 핸들러 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ExceptionUtil exceptionUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        }catch(UnsupportedJwtException e) {
            exceptionUtil.sendErrorResponse(response, CustomErrorCode.ERR_UNSUPPORTED_ACCESS_TOKEN);
        }catch(MalformedJwtException e){
            exceptionUtil.sendErrorResponse(response, CustomErrorCode.ERR_WRONG_ACCESS_TOKEN);
        }catch(SignatureException e){
            exceptionUtil.sendErrorResponse(response, CustomErrorCode.ERR_SIGNATURE_TOKEN);
        }catch(IllegalArgumentException e) {
            exceptionUtil.sendErrorResponse(response, CustomErrorCode.ERR_ILLEGAL_ARGUMENT_ACCESS_TOKEN);
        }catch(AuthenticationException e) {
            exceptionUtil.sendErrorResponse(response, CustomErrorCode.ERR_AUTHENTICATION);
        }catch(AccessDeniedException e) {
            exceptionUtil.sendErrorResponse(response, CustomErrorCode.ERR_ACCESS_DENIED);
        }catch (CustomException e) {
            exceptionUtil.sendErrorResponse(response, e.getError());
        }catch (Exception e) {
            exceptionUtil.sendErrorResponse(response, exceptionUtil.setUnknownError(e));
        }
    }

}

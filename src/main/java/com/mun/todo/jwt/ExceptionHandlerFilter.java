package com.mun.todo.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mun.todo.controller.dto.ErrorCodeDto;
import com.mun.todo.enums.CustomErrorCode;
import com.mun.todo.exception.CustomException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT Filter 앞에 들어 갈 Exception 핸들러 필터
 */
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        }catch (UnsupportedJwtException e) {
            setErrorResponse(response, CustomErrorCode.ERR_UNSUPPORTED_ACCESS_TOKEN);
        }catch (MalformedJwtException e){
            setErrorResponse(response, CustomErrorCode.ERR_WRONG_ACCESS_TOKEN);
        }catch (SignatureException e){
            setErrorResponse(response, CustomErrorCode.ERR_SIGNATURE_TOKEN);
        }catch (IllegalArgumentException e) {
            setErrorResponse(response, CustomErrorCode.ERR_ILLEGAL_ARGUMENT_ACCESS_TOKEN);
        } catch (CustomException e) {
            setErrorResponse(response, e.getError());
        } catch (Exception e) {
            setErrorResponse(response, CustomErrorCode.ERR_UNKNOWN);
        }
    }

    private void setErrorResponse(HttpServletResponse response, CustomErrorCode errorCode) {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json = new ObjectMapper().writeValueAsString(ErrorCodeDto.builder().errorCode(errorCode.getCode()).errorMessage(errorCode.getMessage()).build());
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

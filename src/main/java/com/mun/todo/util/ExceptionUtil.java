package com.mun.todo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mun.todo.controller.dto.ErrorCodeDto;
import com.mun.todo.enums.CustomErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class ExceptionUtil {

    public void sendErrorResponse(HttpServletResponse response, CustomErrorCode errorCode) {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json = new ObjectMapper().writeValueAsString(ErrorCodeDto.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build());
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sendErrorResponse(HttpServletResponse response, ErrorCodeDto errorCodeDto) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json = new ObjectMapper().writeValueAsString(errorCodeDto);
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 정의되지 않은 exception의 경우 exception message를 세팅하여 반환한다.
     * @param e
     * @return
     */
    public ErrorCodeDto setUnknownError(Exception e){

        return ErrorCodeDto.builder()
                .code(CustomErrorCode.ERR_UNKNOWN.getCode())
                .message(e.getMessage())
                .build();
    }
}

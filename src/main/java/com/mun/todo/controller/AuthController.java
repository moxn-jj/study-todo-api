package com.mun.todo.controller;

import com.mun.todo.controller.dto.MemberRequestDto;
import com.mun.todo.controller.dto.MemberResponseDto;
import com.mun.todo.controller.dto.TokenDto;
import com.mun.todo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 해당 컨트롤러는 특정 권한 필요 없음
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * @param memberRequestDto 
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(@RequestBody MemberRequestDto memberRequestDto) {
        return ResponseEntity.ok(authService.signup(memberRequestDto));
    }

    /**
     * 로그인
     * @param memberRequestDto
     * @param response
     * @return
     */
    @PostMapping("/signin")
    public ResponseEntity<TokenDto> signin(@RequestBody MemberRequestDto memberRequestDto, HttpServletResponse response) {

        return ResponseEntity.ok(authService.signin(memberRequestDto, response));
    }

}

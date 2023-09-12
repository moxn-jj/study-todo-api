package com.mun.todo.controller;

import com.mun.todo.controller.dto.MemberRequestDto;
import com.mun.todo.controller.dto.MemberResponseDto;
import com.mun.todo.controller.dto.TokenDto;
import com.mun.todo.controller.dto.TokenRequestDto;
import com.mun.todo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @return
     */
    @PostMapping("/signin")
    public ResponseEntity<TokenDto> signin(@RequestBody MemberRequestDto memberRequestDto) {
        return ResponseEntity.ok(authService.signin(memberRequestDto));
    }

    /**
     * 토큰 재발급
     * @param tokenRequestDto 
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.refresh(tokenRequestDto));
    }

}

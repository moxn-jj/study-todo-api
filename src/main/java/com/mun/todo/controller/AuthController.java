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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(@RequestBody MemberRequestDto memberRequestDto) {
        System.out.println(memberRequestDto.getEmail());
        return ResponseEntity.ok(authService.signup(memberRequestDto));
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenDto> signin(@RequestBody MemberRequestDto memberRequestDto) {
        return ResponseEntity.ok(authService.signin(memberRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(@RequestBody TokenRequestDto tokenRequestDto) {
        System.out.println(tokenRequestDto.getAccessToken());
        return ResponseEntity.ok(authService.refresh(tokenRequestDto));
    }

}

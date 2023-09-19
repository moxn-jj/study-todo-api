package com.mun.todo.service;

import com.mun.todo.controller.dto.MemberRequestDto;
import com.mun.todo.controller.dto.MemberResponseDto;
import com.mun.todo.controller.dto.TokenDto;
import com.mun.todo.entity.Member;
import com.mun.todo.entity.RefreshToken;
import com.mun.todo.enums.CustomErrorCode;
import com.mun.todo.exception.CustomException;
import com.mun.todo.jwt.TokenProvider;
import com.mun.todo.repository.MemberRepository;
import com.mun.todo.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;

/**
 * 회원 비지니스 로직
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 회원가입
     * @param memberRequestDto 
     * @return
     */
    @Transactional
    public MemberResponseDto signup(MemberRequestDto memberRequestDto) {

        if(memberRepository.existsByEmail(memberRequestDto.getEmail())) {
            throw new CustomException(CustomErrorCode.ERR_DUPLICATION_EMAIL);
        }

        // 비밀번호를 인코딩하여 Member 객체에 만듦
        Member member = memberRequestDto.toMember(passwordEncoder);
        // 회원가입 후 리턴되는 Member 객체 중 이메일만 담아서 리턴
        return MemberResponseDto.of(memberRepository.save(member));
    }

    /**
     * 로그인
     * @param memberRequestDto
     * @param response
     * @return
     */
    @Transactional
    public TokenDto signin(MemberRequestDto memberRequestDto, HttpServletResponse response) {

        // 사용자가 입력한 값을 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();

        // 사용자가 입력한 값으로 만들어진 토큰을 통하여 ID/PW를 검증
        // authentication 메서드가 실행될 때 CustomUserDtailsService의 loadUserByUsername()가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // Refresh Token을 DB에 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);

        // refreshToken을 Cookie에 담음
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                .maxAge(7 * 24 * 60 * 60)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());

        // refreshToken 값을 기본값으로 초기화
        tokenDto.resetRefreshTokenForSecure();

        // 로그인이 정상적으로 수행되면 토큰을 리턴
        return tokenDto;
    }

}

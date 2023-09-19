package com.mun.todo.jwt;

import com.mun.todo.controller.dto.TokenDto;
import com.mun.todo.entity.RefreshToken;
import com.mun.todo.enums.CustomErrorCode;
import com.mun.todo.exception.CustomException;
import com.mun.todo.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 유저 정보로 JWT 토큰을 만들거나
 * 토큰으로부터 유저 정보를 가져옴
 */
@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 1; // 1 mins for test
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 30 * 24; // 1 days

    private final Key key;

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * application.yml에 정의한 secret 값을 가져와서 JWT 토큰을 생성함
     *
     * @param secretKey
     * @param refreshTokenRepository
     */
    public TokenProvider(@Value("${jwt.secret}") String secretKey, RefreshTokenRepository refreshTokenRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * 유저 정보를 넘겨받아서 AccessToken과 RefreshToken을 생성함
     * @param authentication
     * @return
     */
    public TokenDto generateTokenDto(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        // 유저 정보, 권한 정보, 만료일자 담음
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // 만료일자만 담음
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiredsIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * JWT 토큰을 복호화하여 토큰에 들어있는 정보를 가져옴
     * @param accessToken
     * @return
     */
    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);

        if(claims.get(AUTHORITIES_KEY) == null) {
            throw new CustomException(CustomErrorCode.ERR_UNAUTHORIZED_REFRESH_TOKEN);
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principle = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principle, "", authorities);
    }

    /**
     * 토큰 정보를 검증하고 만료 되었을 경우 갱신하여 response header에 넣음
     * @param accessToken
     * @return
     */
    public void validateAndUpdateAccessToken(String accessToken, HttpServletRequest request, HttpServletResponse response) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {

        try {

            this.validateToken(accessToken);
        }catch (ExpiredJwtException e) {

            String newAccessToken = this.updateRefreshToken(accessToken, request).getAccessToken();
            response.addHeader("Authorization", "Bearer " + newAccessToken);
        }
    }

    /**
     * token 유효성 검사
     * @param token
     * @throws ExpiredJwtException
     * @throws UnsupportedJwtException
     * @throws MalformedJwtException
     * @throws IllegalArgumentException
     */
    private boolean validateToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {

        try {

            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // JWT 모듈이 알아서 Exception을 던져줌

            return true;
        }catch (ExpiredJwtException e) { // Exception when having the expired token

            log.info("만료된 JWT 토큰입니다.");
            throw e;
        }catch (UnsupportedJwtException e) { // Exception when having the unsupported token

            log.info("지원되지 않는 JWT 토큰입니다.");
            throw e;
        }catch (MalformedJwtException e){ // Exception when having the invalid token structure

            log.info("잘못된 JWT 서명입니다.");
            throw e;
        }catch (SignatureException e){ // an error occurs during the signature or verification process

            log.info("잘못된 JWT 서명입니다.");
            throw e;
        }catch (IllegalArgumentException e) { // Exception when passed the inappropriate argument

            log.info("JWT 토큰이 잘못되었습니다.");
            throw e;
        }
    }

    /**
     * token에서 정보를 추출
     * @param token
     * @return
     */
    private Claims parseClaims(String token) {

        try {

            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e) {

            return e.getClaims();
        }
    }

    /**
     * cookie에 있는 refresh token을 가져옴
     * @param request
     * @return
     */
    private String getRefreshTokenInCookie(HttpServletRequest request) {

        String refreshTokenInCookie = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshTokenInCookie = cookie.getValue();
            }
        }

        return refreshTokenInCookie;
    }
    /**
     * accessToken을 갱신
     * @param accessToken
     * @param request
     * @return
     */
    @Transactional
    public TokenDto updateRefreshToken(String accessToken, HttpServletRequest request) {

        String refreshTokenInCookie = this.getRefreshTokenInCookie(request);

        // cookie에 담겨있던 Refresh Token 검증
        if(!this.validateToken(refreshTokenInCookie)) {
            throw new CustomException(CustomErrorCode.ERR_INVALID_REFRESH_TOKEN);
        }

        // access token에서 사용자 정보(member id) 가져옴
        Authentication authentication = this.getAuthentication(accessToken);

        // DB에서 member id를 기반으로 refresh token 값 가져옴
        RefreshToken refreshTokenInDB = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new CustomException(CustomErrorCode.ERR_ALREADY_LOGOUT)); // 이미 로그아웃된 사용자의 경우

        // db에 저장되어 있는 refresh token과 사용자가 보낸 refresh token이 같은지 비교
        if(!refreshTokenInDB.getValue().equals(refreshTokenInCookie)) {
            throw new CustomException(CustomErrorCode.ERR_INVALID_TOKEN_USER);
        }

        // 새로운 토큰 생성
        TokenDto tokenDto = this.generateTokenDto(authentication);

        // db에 새로운 토큰 업데이트
        RefreshToken newRefreshToken = refreshTokenInDB.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }
}

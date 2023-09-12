package com.mun.todo.jwt;

import com.mun.todo.controller.dto.TokenDto;
import com.mun.todo.enums.CustomErrorCode;
import com.mun.todo.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30 mins
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 30 * 24 * 7; // 7 days

    private final Key key;

    /**
     * application.yml에 정의한 secret 값을 가져와서 JWT 토큰을 생성함
     * @param secretKey
     */
    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
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
     * 토큰 정보를 검증
     * @param token
     * @return
     */
    public boolean validateToken(String token) {

        try {

            // JWT 모듈이 알아서 Exception을 던져줌
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

        // SecurityException : Exception when having the invalid jwt signature
        // MalformedJwtException : Exception when having the invalid token structure
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){

            log.info("잘못된 JWT 서명입니다.");

        // ExpiredJwtException : Exception when having the expired token
        }catch (ExpiredJwtException e) {

            log.info("만료된 JWT 토큰입니다.");

        // UnsupportedJwtException : Exception when having the unsupported token
        }catch (UnsupportedJwtException e) {

            log.info("지원되지 않는 JWT 토큰입니다.");

        // IllegalArgumentException : Exception when passed the inappropriate argument
        }catch (IllegalArgumentException e) {

            log.info("JWT 토큰이 잘못되었습니다.");
        }

        return true;
    }

    private Claims parseClaims(String accessToken) {

        try {

            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        }catch (ExpiredJwtException e) {

            return e.getClaims();
        }
    }
}

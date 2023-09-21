package com.mun.todo.jwt;

import com.mun.todo.config.SecurityConfig;
import com.mun.todo.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Request 앞에 넣을 custom filter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    /**
     * 실제로 필터링 로직을 수행하는 곳으로
     * Request header로 부터 토큰을 꺼내고 정상적인 경우 SecurityContext에 담음
     * 회원가입, 로그인, 재발급을 제외한 모든 Request 요청은 해당 필터를 거침
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, CustomException {

        // permitAll()로 설정한 경로는 토큰 검사를 하지 않음
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String uri = request.getRequestURI();
        for(String path : SecurityConfig.PUBLIC_PATHS){
            if (pathMatcher.match(path, uri)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 1. request의 header에서 토큰 꺼내기
        String accessToken = resolveToken(request);
        if(StringUtils.hasText(accessToken)){

            // 2. 토큰 유효성 검사하고 만료되었으면 갱신하기
            tokenProvider.validateAndUpdateAccessToken(accessToken, request, response);

            // 2-1. 토큰에서 authentication 객체 가지고 오기
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            // 2-2. 가져온 인증 객체 (authentication)를 SecurityContextHolder에 담기
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }else {
            throw new AuthenticationException("AcessToken is null") {};
        }

    }

    /**
     * Reauest Header에서 토큰 정보 꺼내오기
     * @param request
     * @return
     */
    private String resolveToken(HttpServletRequest request){

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }

        return null;
    }

}

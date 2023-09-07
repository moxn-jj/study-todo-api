package com.mun.todo.config;

import com.mun.todo.jwt.JwtAccessDeniedHandler;
import com.mun.todo.jwt.JwtAuthenticationEntryPoint;
import com.mun.todo.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 스프링 시큐리티 설정
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring()
                .antMatchers("/h2-console/**", "/favicon.ico", "/api/todos"); // TODO : /api/todos 는 잠시 테스트 용도로 추가
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CSRF 설정 disable : CSRF(사이트 간 요청 위조)? 사용자가 자신의 의지와 무관하게 공격자가 의도한 행위를 웹사이트에 요청하게 하는 것
        // jwt 토큰을 통햐여 인증 관리를 하기 때문에 stateless 하기 때문에 disabled 해도 ok.
        http.csrf().disable()

                // exception handler 커스텀
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 동일 출처의 경우 frame 랜더링 허용
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 시큐리티는 세션 사용이 기본이나, 여기서는 사용하지 않기 때문에 stateless로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 비회원 접근 가능 페이지 설정
                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()

                // jwt 필터를 등록했던 JwtSecurityConfig 클래스 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }

}

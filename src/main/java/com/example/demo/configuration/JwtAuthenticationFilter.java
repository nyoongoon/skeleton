package com.example.demo.configuration;

import com.example.demo.domain.token.service.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //OncePerRequestFilter -> 한 요청당 한번 필터 실행..
    // Http 프로토콜에서 헤더에 포함 되는데, 어떤 key에 토큰을 줄건지 설정
    public static final String TOKEN_HEADER = "Authorization";
    // 인증 타입 설정: jwt -> Bearer
    public static final String TOKEN_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;
    @Value("{spring.jwt.access-secret-key}")
    private String accessSecretKey;


    // 요청 -> filter -> servlet -> interceptor -> aop -> controller
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.resolveTokenFromRequest(request);
        // 토큰 유효성 검증
        if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token, accessSecretKey)) {
            Authentication auth = this.tokenProvider.getAuthentication(token); // 인증 정보 가져오기
            SecurityContextHolder.getContext().setAuthentication(auth); // 시큐리티 컨텍스트에 인증 정보 담기
        }
        filterChain.doFilter(request, response);
    }

    // 헤더에서 토큰 얻기
    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);
        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}

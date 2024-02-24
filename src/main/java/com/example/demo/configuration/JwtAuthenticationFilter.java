package com.example.demo.configuration;

import com.example.demo.utility.token.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //OncePerRequestFilter -> 한 요청당 한번 필터 실행..

    private final TokenProvider tokenProvider; // header와 cookie 관련 로직은 authAppService로 옮기는게 맞지 않을까..?

    // 요청 -> filter -> servlet -> interceptor -> aop -> controller
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = this.tokenProvider.resolveAccessToken(request);
        boolean isAccessTokenValidate = this.tokenProvider.validateAccessToken(accessToken);
        String refreshToken = this.tokenProvider.resolveRefreshToken(request);
        boolean isRefreshTokenValidate = this.tokenProvider.validateRefreshToken(refreshToken);

        if (isAccessTokenValidate) {
            // 엑세스 토큰 유효
            this.setAuthenticationByAccessToken(accessToken);
        } else if (isRefreshTokenValidate) {
            // 리프레시 토큰 유효
            accessToken = this.tokenProvider.getAccessToken(refreshToken);
            this.setAuthenticationByAccessToken(accessToken);
            this.tokenProvider.addAccessTokenToHeader(response, accessToken);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationByAccessToken(String accessToken) {
        Authentication auth = this.tokenProvider.getAuthentication(accessToken); // 인증 정보 가져오기
        SecurityContextHolder.getContext().setAuthentication(auth); // 시큐리티 컨텍스트에 인증 정보 담기
    }
}

package com.example.demo.utility.token;

import com.example.demo.application.auth.dto.TokenDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TokenProvider {
    // 토큰 발급
    TokenDto getTokens(String username, List<String> roles);

    String getRefreshSecretKey(Claims claims);

    String getAccessToken(String refreshToken);

//    String generateToken(Claims claims, String secretKey, long expiredTime);

    // 권한 얻기
    Authentication getAuthentication(String jwt);

    boolean validateToken(String token, String secretKey);

    boolean validateAccessToken(String token);

    boolean validateRefreshToken(String token);

    void addAccessTokenToHeader(HttpServletResponse response, String accessToken);

    void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken);

    String resolveAccessToken(HttpServletRequest request);

    String resolveRefreshToken(HttpServletRequest request);

}

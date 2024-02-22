package com.example.demo.domain.token.service;

import com.example.demo.application.auth.dto.TokenDto;
import com.example.demo.domain.token.entity.Token;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TokenProvider {
    // 토큰 발급
    TokenDto getToken(String username, List<String> roles);

    String generateToken(Claims claims, String secretKey, long expiredTime);

    // 권한 얻기
    Authentication getAuthentication(String jwt);

    boolean validateToken(String token, String secretKey);

    String validateRefreshToken(Token entity);
}

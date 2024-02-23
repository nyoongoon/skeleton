package com.example.demo.application.auth.controller;

import com.example.demo.application.auth.dto.AuthDto;
import com.example.demo.application.auth.dto.TokenDto;
import com.example.demo.application.auth.service.AuthAppService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthAppService authAppService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody AuthDto.SignUp signUp) {
        this.authAppService.signup(signUp);
        return ResponseEntity.ok().build();
    }

    // 인증 및 토큰 리턴
    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody AuthDto.SignIn signIn,
                                         HttpServletResponse response) {
        TokenDto tokenDto = authAppService.signin(signIn);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true); // JavaScript에서 쿠키에 접근 불가능하도록 설정
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30); // 쿠키 유효 기간 설정 (예: 30일)
        response.addCookie(refreshTokenCookie);

        response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + tokenDto.getAccessToken());

        return ResponseEntity.ok(tokenDto.getAccessToken());
    }


    // 리프레시 토큰 검증
    @GetMapping("/refresh")
    public ResponseEntity<String> renewalAccessToken(@CookieValue String refreshToken,
                                                     HttpServletResponse response) {
        String accessToken = authAppService.renewalAccessToken(refreshToken);

        return ResponseEntity.ok(accessToken);
    }
}

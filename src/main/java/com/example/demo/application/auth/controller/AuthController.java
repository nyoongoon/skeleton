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
        TokenDto tokenDto = authAppService.signin(signIn, response);
        //TODO 쿠키는 응답에 담았을 경우 포스트맨 요청에 포함되는 이유? (헤더는 안됨)
        return ResponseEntity.ok(tokenDto.getAccessToken());
    }


    // 리프레시 토큰 검증
    @GetMapping("/refresh")
    public ResponseEntity<String> renewalAccessToken(@CookieValue String refreshToken,
                                                     HttpServletResponse response) {
        String accessToken = authAppService.renewalAccessToken(refreshToken, response);

        return ResponseEntity.ok(accessToken);
    }
}

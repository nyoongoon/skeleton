package com.example.demo.application.auth.controller;

import com.example.demo.application.auth.dto.AuthDto;
import com.example.demo.application.auth.dto.TokenDto;
import com.example.demo.application.auth.service.AuthAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<TokenDto> signin(@RequestBody AuthDto.SignIn signIn) {
        TokenDto tokenDto = authAppService.signin(signIn);
        return ResponseEntity.ok(tokenDto);
    }


    // 리프레시 토큰 검증
    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> renewalAccessToken(@RequestBody String refreshToken) {
        TokenDto tokenDto = authAppService.renewalAccessToken();

        return ResponseEntity.ok(tokenDto);
    }
}

package com.example.demo.application.controller.auth;

import com.example.demo.application.service.auth.AuthAppService;
import com.example.demo.application.dto.auto.AuthDto;
import com.example.demo.application.dto.auto.TokenDto;
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

    // 인증 및 토큰 반환
    @PostMapping("/signin")
    public ResponseEntity<TokenDto> signin(@RequestBody AuthDto.SignIn signIn) {
        TokenDto tokenDto = authAppService.signin(signIn);
        return ResponseEntity.ok(tokenDto);
    }
}

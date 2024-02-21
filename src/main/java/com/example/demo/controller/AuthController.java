package com.example.demo.controller;

import com.example.demo.domain.Member;
import com.example.demo.dto.auto.Auth;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.MemberService;
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
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody Auth.SignUp signUp) {
        this.memberService.register(signUp);
        return ResponseEntity.ok().build();
    }

    // 인증 및 토큰 반환
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn signIn) {
        // 패스워드 인증
        Member member = memberService.authenticate(signIn);
        // 토큰 생성
        String token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());
        return ResponseEntity.ok(token);
    }
}

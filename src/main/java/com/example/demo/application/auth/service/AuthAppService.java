package com.example.demo.application.auth.service;

import com.example.demo.application.auth.dto.AuthDto;
import com.example.demo.application.auth.dto.TokenDto;
import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.member.service.MemberDomainService;
import com.example.demo.domain.token.entity.RefreshToken;
import com.example.demo.domain.token.service.RefreshTokenDomainService;
import com.example.demo.exception.auth.TokenExpiredException;
import com.example.demo.utility.token.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAppService {

    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberDomainService memberDomainService;
    private final RefreshTokenDomainService refreshTokenDomainService;

    @Transactional
    public void signup(AuthDto.SignUp signUp) {
        // 존재여부 판단
        boolean isExists = memberDomainService.findByUsername(signUp.getUsername()).isPresent();
        if (isExists) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        // 패스워드 암호화
        String encodedPassword = this.passwordEncoder.encode(signUp.getPassword());
        signUp.setPassword(encodedPassword);
        // 엔티티 변환
        Member member = signUp.toMemberEntity();
        // 등록
        memberDomainService.regist(member);
    }

    @Transactional
    public TokenDto signin(AuthDto.SignIn signIn, HttpServletResponse response) {
        //멤버 조회
        Member member = this.memberDomainService.findByUsername(signIn.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        // 패스워드 일치 여부
        boolean isMatches = this.isPasswordMatches(signIn.getPassword(), member.getPassword());
        if (!isMatches) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 토큰 Dto 생성
        TokenDto tokenDto = this.tokenProvider.getTokens(member.getUsername(), member.getRoles());
        // 리프레시 토큰 엔티티 생성 및 신규 저장
        RefreshToken refreshToken = tokenDto.toRefreshTokenEntity();
        this.refreshTokenDomainService.renewalRefreshToken(refreshToken);

        // 토큰 헤더, 쿠키에 저장
        this.tokenProvider.addAccessTokenToHeader(response, tokenDto.getAccessToken());
        this.tokenProvider.addRefreshTokenToCookie(response, tokenDto.getRefreshToken());

        return tokenDto;
    }

    // 액세스 토큰 재발급 - 헤더에 삽입
    public String renewalAccessToken(String refreshToken, HttpServletResponse response) {
        boolean isValidate = this.tokenProvider.validateRefreshToken(refreshToken);
        if (!isValidate) {
            throw new TokenExpiredException("토큰이 만료되었습니다.");
        }
        String accessToken = this.tokenProvider.getAccessToken(refreshToken);

        this.tokenProvider.addAccessTokenToHeader(response, accessToken);
        return accessToken;
    }


    public boolean isPasswordMatches(String originPassword, String encryptedPassword) {
        return this.passwordEncoder.matches(originPassword, encryptedPassword);
    }
}

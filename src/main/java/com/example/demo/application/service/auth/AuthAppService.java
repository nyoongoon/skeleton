package com.example.demo.application.service.auth;

import com.example.demo.application.dto.auto.AuthDto;
import com.example.demo.application.dto.auto.TokenDto;
import com.example.demo.configuration.token.TokenProvider;
import com.example.demo.domain.entity.Member;
import com.example.demo.domain.service.member.MemberDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAppService {

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final MemberDomainService memberDomainService;

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

    public TokenDto signin(AuthDto.SignIn signIn) {
        Member member = memberDomainService.findByUsername(signIn.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        // 패스워드 일치 여부
        boolean isMatches = isPasswordMatches(signIn.getPassword(), member.getPassword());
        if (!isMatches) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 토큰 생성
        return this.tokenProvider.getToken(member.getUsername(), member.getRoles()) //여기서부터 보기..
    }


    public boolean isPasswordMatches(String originPassword, String encryptedPassword) {
        return this.passwordEncoder.matches(originPassword, encryptedPassword);
    }
}

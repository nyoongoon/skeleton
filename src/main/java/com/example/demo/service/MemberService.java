package com.example.demo.service;

import com.example.demo.domain.Member;
import com.example.demo.dto.auto.Auth;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder; //구현 클래스 설정 필요
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Member 엔티티 UserDetails 상속하고 있음..
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("에러 메시지 작성"));
    }

    // 가입 -> Member 저장
    public Member register(Auth.SignUp signUp) {
        boolean exists = this.memberRepository.existsByUsername(signUp.getUsername());
        if (exists) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        // 패스워드 암호화
        signUp.setPassword(this.passwordEncoder.encode(signUp.getPassword()));
        return this.memberRepository.save(signUp.toMemberEntity());
    }

    // 인증
    public Member authenticate(Auth.SignIn signIn) {
        Member member = this.memberRepository.findByUsername(signIn.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        // 패스워드 인코딩 전후 동일 비교
        if (!this.passwordEncoder.matches(member.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }
}

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

    public Member register(Auth.SignUp signUp) {
        boolean exists = this.memberRepository.existsByUsername(signUp.getUsername());
        if (exists) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        // 패스워드 암호화
        signUp.setPassword(this.passwordEncoder.encode(signUp.getPassword()));
        return this.memberRepository.save(signUp.toMemberEntity());
    }

    public Member authenticate(Auth.SignIn signIn) {

        return null;
    }
}

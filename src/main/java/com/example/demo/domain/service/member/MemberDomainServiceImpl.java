package com.example.demo.domain.service.member;

import com.example.demo.domain.entity.Member;
import com.example.demo.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberDomainServiceImpl implements MemberDomainService, UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Member 엔티티 UserDetails 상속하고 있음..
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("에러 메시지 작성"));
    }

    // 가입 -> Member 저장
    @Override
    public void regist(Member member) {
        this.memberRepository.save(member);
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        return this.memberRepository.findByUsername(username);
    }
}

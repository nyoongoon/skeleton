package com.example.demo.domain.service.member;

import com.example.demo.domain.entity.Member;

import java.util.Optional;

public interface MemberDomainService {
    void regist(Member member);
    Optional<Member> findByUsername(String username);
}

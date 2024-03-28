package com.example.demo.domain.member.service;

import com.example.demo.domain.member.entity.Member;

import java.util.Optional;

public interface MemberDomainService {
    void regist(Member member);
    boolean isMemberExists(String username);
    Member findMemberByUsername(String username);
}

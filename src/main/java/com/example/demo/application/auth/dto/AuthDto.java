package com.example.demo.application.auth.dto;

import com.example.demo.domain.member.entity.Member;
import lombok.Data;

import java.util.List;

public class AuthDto {
    @Data
    public static class SignIn {
        private String username;
        private String password;
    }

    @Data
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles;

        public Member toMemberEntity() {
            return Member.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }

}

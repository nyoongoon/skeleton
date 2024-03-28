package com.example.demo.application.auth.dto;

import com.example.demo.application.auth.constants.Authority;
import com.example.demo.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AuthDto {
    @Getter
    public static class SignIn {
        private String username;
        private String password;

        @Builder
        public SignIn(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @Getter
    @Setter
    public static class SignUp {
        private String username;
        private String password;
        private List<Authority> roles;

        @Builder
        public SignUp(String username, String password, List<Authority> roles) {
            this.username = username;
            this.password = password;
            this.roles = roles;
        }

        public Member toMemberEntity() {
            return Member.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }

}

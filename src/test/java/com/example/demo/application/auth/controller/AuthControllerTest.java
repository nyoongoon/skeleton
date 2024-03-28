package com.example.demo.application.auth.controller;

import com.example.demo.application.auth.constants.Authority;
import com.example.demo.application.auth.dto.AuthDto.SignIn;
import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.member.service.MemberDomainService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.example.demo.application.auth.constants.Authority.ROLE_READ;
import static com.example.demo.application.auth.constants.Authority.ROLE_WRITE;
import static com.example.demo.application.auth.dto.AuthDto.SignUp;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberDomainService memberDomainService;

    private SignUp signUp = SignUp.builder()
            .username("abcd")
            .password("abcd1234")
            .roles(List.of(ROLE_READ, ROLE_WRITE))
            .build();

    @Test
    void 회원가입_테스트() throws Exception {
        String json = objectMapper.writeValueAsString(this.signUp);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Member member = memberDomainService.findMemberByUsername(this.signUp.getUsername());

        assertThat(this.signUp.getUsername()).isEqualTo(member.getUsername());
        for (Authority authority : this.signUp.getRoles()) {
            assertThat(member.getRoles().contains(authority)).isTrue();
        }
    }

    @Test
    void 로그인_테스트() throws Exception {
        memberDomainService.regist(this.signUp.toMemberEntity());

        SignIn signin = SignIn.builder()
                .username(this.signUp.getUsername())
                .password(this.signUp.getPassword())
                .build();
        String json = objectMapper.writeValueAsString(signin);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())

        //TODO 헤더 에서 토큰 찾기..
    }
}
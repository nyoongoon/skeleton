package com.example.demo.application.auth.controller;

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

    @Test
    void 회원가입_테스트() throws Exception {
        SignUp signUp = SignUp.builder()
                .username("abcd")
                .password("abcd1234")
                .roles(List.of(ROLE_READ, ROLE_WRITE))
                .build();
        String json = objectMapper.writeValueAsString(signUp);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""))
                .andDo(MockMvcResultHandlers.print());

        Member member = memberDomainService.findMemberByUsername(signUp.getUsername());

        assertThat(signUp.getUsername()).isEqualTo(member.getUsername());
    }
}
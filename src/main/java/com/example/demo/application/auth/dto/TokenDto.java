package com.example.demo.application.auth.dto;

import com.example.demo.domain.token.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDto {
    private String username;
    private String accessToken;
    private String refreshToken;

    public RefreshToken toRefreshTokenEntity() {
        return RefreshToken.builder()
                .username(this.username)
                .tokenValue(this.refreshToken)
                .build();
    }
}

package com.example.demo.domain.token.service;

import com.example.demo.domain.token.entity.RefreshToken;

public interface RefreshTokenDomainService {
    void renewalRefreshToken(RefreshToken refreshToken);
}

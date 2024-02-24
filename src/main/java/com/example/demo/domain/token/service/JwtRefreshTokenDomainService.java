package com.example.demo.domain.token.service;

import com.example.demo.domain.token.entity.RefreshToken;
import com.example.demo.domain.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtRefreshTokenDomainService implements RefreshTokenDomainService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void renewalRefreshToken(RefreshToken refreshToken) {
        String username = refreshToken.getUsername();
        boolean isExists = refreshTokenRepository.findByUsername(username).isPresent();
        if (isExists) { // username으로 등록된 리프레시 토큰 존재하면 삭제
            refreshTokenRepository.deleteByUsername(username);
        }
        // 새로 생성
        refreshTokenRepository.save(refreshToken);
    }
}

package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final long TOKEN_EXPIRED_TIME = 1000 * 60 * 60; //밀리세컨드*초*분 == 1시간
    private static final String KEY_ROLES = "roles";
    private final UserDetailsService userDetailsService;
    @Value("{spring.jwt.secret}")
    private String secretKey;

    // 토큰 발급
    public String generateToken(String username, List<String> roles) {
        // 사용자의 권한정보를 저장하기 위한 클레임 생성
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles); // 클레임은 키밸류
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRED_TIME);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expiredDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 시그니처 알고리즘, 비밀키
                .compact();
    }

    // 권한 얻기
    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(this.getUsername(jwt));

        //스프링에서 지원해주는 형태의 토큰 -> 사용자 정보, 사용자 권한 정보
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());
    }

    // 토큰 유효성 체크
    private Claims parseClaims(String token) {
        // 토큰 만료 경우 예외 발생
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }

    }
}

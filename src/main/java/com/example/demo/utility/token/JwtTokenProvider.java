package com.example.demo.utility.token;

import com.example.demo.application.auth.dto.TokenDto;
import com.example.demo.domain.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {
    // Http 프로토콜에서 헤더에 포함 되는데, 어떤 key에 토큰을 줄건지 설정
    private static final String TOKEN_HEADER = "Authorization";
    // 인증 타입 설정: jwt -> Bearer
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRED_TIME = 1000 * 60 * 30; //밀리세컨드*초*분* == 30분
    private static final long REFRESH_TOKEN_EXPIRED_TIME = 1000 * 60 * 60 * 24; //밀리세컨드*초*분*시 == 24시간
    private static final String KEY_ROLES = "roles";
    private final UserDetailsService userDetailsService;

    @Value("{spring.jwt.access-secret-key}")
    private String accessSecretKey;

    @Value("{spring.jwt.refresh-secret-key}")
    private String refreshSecretKey;

    // 토큰 발급
    @Override
    public TokenDto getTokens(Member member) {
        // 사용자의 권한정보를 저장하기 위한 클레임 생성
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles); // 클레임은 키밸류

        //Access Token & Refresh Token
        String refreshToken = this.getRefreshSecretKey(claims);
        String accessToken = this.getAccessToken(refreshToken);

        return new TokenDto(username, accessToken, refreshToken);
    }

    public String getRefreshSecretKey(Claims claims){
        return this.generateToken(claims, this.refreshSecretKey, REFRESH_TOKEN_EXPIRED_TIME);
    }

    @Override
    public String getAccessToken(String refreshToken) {
        // 사용자의 권한정보를 저장하기 위한 클레임 생성
        Claims claims = this.parseClaims(refreshToken, refreshSecretKey);
        // 엑세스 토큰 재발급
        return this.generateToken(claims, accessSecretKey, ACCESS_TOKEN_EXPIRED_TIME);
    }

    @Override
    private String generateToken(Claims claims, String secretKey, long expiredTime) {
        Date now = new Date();
        //jwt Token
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + expiredTime)) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 시그니처 알고리즘, 비밀키
                .compact();
    }

    // 권한 얻기
    @Override
    public Authentication getAuthentication(String token) {
        String username = this.parseClaims(token, this.accessSecretKey).getSubject();
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        //스프링에서 지원해주는 형태의 토큰 -> 사용자 정보, 사용자 권한 정보
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Override
    public boolean validateToken(String token, String secretKey) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = this.parseClaims(token, secretKey);
        return !claims.getExpiration().before(new Date());
    }

    @Override
    public boolean validateAccessToken(String token) {
        return validateToken(token, this.accessSecretKey);
    }

    @Override
    public boolean validateRefreshToken(String token) {
        return validateToken(token, this.refreshSecretKey);
    }

//    @Override
//    public String validateRefreshToken(RefreshToken entity) { // refreshToken 검증하기 -> generateToken()메소드 사용
//        String refreshToken = entity.getTokenValue();
//        if (validateToken(refreshToken, this.refreshSecretKey)) {
//            //refresh 토큰의 만료시간이 지나지 않았을 경우, 새로운 access 토큰을 생성합니다.
//            Claims claims = this.parseClaims(refreshToken, this.refreshSecretKey);
//            return generateToken(claims, accessSecretKey, ACCESS_TOKEN_EXPIRED_TIME);
//        }
//        return null;
//    }

    // 토큰 유효성 체크
    private Claims parseClaims(String token, String secretKey) {
        // 토큰 만료 경우 예외 발생
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


    // 헤더에서 엑세스 토큰 얻기
    public String resolveAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader(TOKEN_HEADER);
        if (!ObjectUtils.isEmpty(accessToken) && accessToken.startsWith(TOKEN_PREFIX)) {
            return accessToken.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    // 쿠키에서 리프레시 토큰 얻기q
    public String resolveRefreshToken(HttpServletRequest request) {
        Cookie refreshTokenCookie = WebUtils.getCookie(request, "refreshToken");
        if (refreshTokenCookie != null) {
            return refreshTokenCookie.getValue();
        }
        return null;
    }

    // 헤더에 엑세스 토큰 심기 //TODO
    public void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
        response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + accessToken);
    }

    @Override
    public void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // JavaScript에서 쿠키에 접근 불가능하도록 설정
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30); // 쿠키 유효 기간 설정 (예: 30일)
        response.addCookie(refreshTokenCookie);
//        response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + refreshToken);
    }
}

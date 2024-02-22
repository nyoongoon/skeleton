package com.example.demo.domain.token.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@ToString
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @Column(name = "KEY_EMAIL", nullable = false)
    // UNIQUE
    private String username; //TODO 외래키?

    //    @Column(name = "REFRESH_TOKEN", nullable = false)
    private String tokenValue;

    @Builder
    public RefreshToken(String username, String tokenValue) {
        this.username = username;
        this.tokenValue = tokenValue;
    }
}

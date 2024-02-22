package com.example.demo.domain.token.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@ToString
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @Column(name = "KEY_EMAIL", nullable = false)
    private String username; //TODO 외래키?

//    @Column(name = "REFRESH_TOKEN", nullable = false)
    private String tokenValue;
}

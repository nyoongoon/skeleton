package com.example.demo.domain.token.repository;

import com.example.demo.domain.token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}

package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
//    @Bean
//    public PasswordEncoder passwordEncoder(){ --> security config에 가는게 낫지 않을까..?
//        return new BCryptPasswordEncoder();
//    }
}

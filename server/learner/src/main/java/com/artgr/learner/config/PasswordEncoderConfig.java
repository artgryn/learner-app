package com.artgr.learner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// BCrypt hashing only - used to store password hashes on register/reset
// (doc/prompt_2.md). Does NOT enable Spring Security or any auth filter
// chain; auth stays stubbed (see doc/CLAUDE.md, CurrentUserProvider).
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

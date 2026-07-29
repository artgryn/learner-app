package com.artgr.learner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

// A single injectable Random seam so shuffling (letters, options - always
// server-side per doc/CLAUDE.md) is swappable for a seeded instance in
// tests, while production gets genuine randomness.
@Configuration
public class RandomConfig {

    @Bean
    public Random random() {
        return new Random();
    }
}

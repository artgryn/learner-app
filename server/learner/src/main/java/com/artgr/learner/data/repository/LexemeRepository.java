package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.Lexeme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LexemeRepository extends JpaRepository<Lexeme, Long> {
}

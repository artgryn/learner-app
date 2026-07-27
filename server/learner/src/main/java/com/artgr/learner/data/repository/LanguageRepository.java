package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, String> {
}

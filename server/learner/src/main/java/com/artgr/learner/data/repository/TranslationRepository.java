package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.Translation;
import com.artgr.learner.data.entity.TranslationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, TranslationId> {
}

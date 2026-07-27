package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.WordForm;
import com.artgr.learner.data.entity.WordFormId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordFormRepository extends JpaRepository<WordForm, WordFormId> {
}

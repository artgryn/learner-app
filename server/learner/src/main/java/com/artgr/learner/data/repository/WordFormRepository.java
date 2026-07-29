package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.WordForm;
import com.artgr.learner.data.entity.WordFormId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordFormRepository extends JpaRepository<WordForm, WordFormId> {

    @Query("select wf from WordForm wf where wf.id.lexemeId = :lexemeId")
    List<WordForm> findByLexemeId(@Param("lexemeId") Long lexemeId);
}

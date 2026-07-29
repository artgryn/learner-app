package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.Translation;
import com.artgr.learner.data.entity.TranslationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TranslationRepository extends JpaRepository<Translation, TranslationId> {

    // Undirected: lexemeId may be on either side (lexeme_a < lexeme_b is a
    // storage detail, not a query constraint - doc/app-info/Data/translation.md).
    @Query("select t from Translation t where t.lexemeA.id = :lexemeId or t.lexemeB.id = :lexemeId")
    List<Translation> findByLexemeId(@Param("lexemeId") Long lexemeId);
}

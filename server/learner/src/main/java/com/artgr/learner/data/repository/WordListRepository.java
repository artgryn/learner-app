package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.WordList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordListRepository extends JpaRepository<WordList, Long> {

    // "Supported combos are emergent from data" (doc/api/swagger.yaml /lists):
    // a list qualifies only if at least one of its words has a translation
    // into the base language - not a declared list of allowed pairs.
    @Query(value = """
            SELECT DISTINCT wl.* FROM list wl
            JOIN list_item li ON li.list_id = wl.id
            JOIN translation t ON t.lexeme_a = li.lexeme_id OR t.lexeme_b = li.lexeme_id
            JOIN lexeme other ON other.id = CASE WHEN t.lexeme_a = li.lexeme_id THEN t.lexeme_b ELSE t.lexeme_a END
            WHERE wl.target_lang = :target AND other.lang = :base
            """, nativeQuery = true)
    List<WordList> findByTargetLangWithTranslationCoverage(@Param("target") String target, @Param("base") String base);
}

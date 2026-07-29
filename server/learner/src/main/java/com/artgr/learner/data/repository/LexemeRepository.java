package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.Lexeme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LexemeRepository extends JpaRepository<Lexeme, Long> {

    // Distractor pool: same language, same pos, nearest freq_rank first,
    // excluding the answer itself (doc/CLAUDE.md - "same target-language,
    // same pos, near freq_rank"). No any-pos fallback - a word without
    // enough same-pos siblings simply isn't eligible for that exercise type.
    @Query("select l from Lexeme l where l.language.code = :lang and l.pos = :pos and l.id <> :excludeId "
            + "order by abs(coalesce(l.freqRank, 999999) - :freqRank)")
    List<Lexeme> findDistractorCandidates(
            @Param("lang") String lang,
            @Param("pos") String pos,
            @Param("excludeId") Long excludeId,
            @Param("freqRank") int freqRank
    );
}

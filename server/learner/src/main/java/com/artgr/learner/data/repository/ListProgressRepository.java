package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.ListProgress;
import com.artgr.learner.data.entity.ListProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListProgressRepository extends JpaRepository<ListProgress, ListProgressId> {

    // The one and only "which words does this user have progress on" query -
    // used both for session candidate selection (SessionScheduler) and for
    // computing exercisesCompleted (below). Deliberately NOT time-filtered:
    // the app must never block practice with a due-date cooldown - see
    // SessionScheduler.selectCandidates.
    @Query("select lp from ListProgress lp where lp.id.userId = :userId and lp.id.listId = :listId")
    List<ListProgress> findByUserIdAndListId(@Param("userId") Long userId, @Param("listId") Long listId);

    @Query("select count(lp) from ListProgress lp "
            + "where lp.id.userId = :userId and lp.id.listId = :listId and lp.timesPracticed >= :threshold")
    long countMastered(@Param("userId") Long userId, @Param("listId") Long listId, @Param("threshold") int threshold);

    // Sum of each word's practice count, capped at masteryThreshold per word -
    // "exercises completed toward mastering this list" (extra reps past
    // mastery don't push progress past 100%). threshold is always passed in
    // from app config (SessionProperties.masteryThreshold, currently 3 for
    // MVP but expected to differ in production) - never hardcode it here.
    @Query("select coalesce(sum(case when lp.timesPracticed > :threshold then :threshold else lp.timesPracticed end), 0) "
            + "from ListProgress lp where lp.id.userId = :userId and lp.id.listId = :listId")
    long sumCappedTimesPracticed(@Param("userId") Long userId, @Param("listId") Long listId, @Param("threshold") int threshold);

    // Distinct lexeme across ALL lists for this user ("words known" is not
    // list-scoped even though progress itself is - doc/app-info/MVP.md).
    @Query("select count(distinct lp.lexeme.id) from ListProgress lp "
            + "where lp.id.userId = :userId and lp.timesPracticed >= :threshold")
    long countDistinctMasteredLexemes(@Param("userId") Long userId, @Param("threshold") int threshold);
}

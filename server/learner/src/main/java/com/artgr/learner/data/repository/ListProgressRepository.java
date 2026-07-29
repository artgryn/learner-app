package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.ListProgress;
import com.artgr.learner.data.entity.ListProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface ListProgressRepository extends JpaRepository<ListProgress, ListProgressId> {

    @Query("select lp from ListProgress lp where lp.id.userId = :userId and lp.id.listId = :listId")
    List<ListProgress> findByUserIdAndListId(@Param("userId") Long userId, @Param("listId") Long listId);

    // due IS NULL counts as "due now" - a row can have no due date yet
    // (never scheduled), which should still be eligible for review, not
    // permanently excluded. Ordered so the most overdue reviews come first.
    @Query("select lp from ListProgress lp where lp.id.userId = :userId and lp.id.listId = :listId "
            + "and (lp.due is null or lp.due <= :now) order by lp.due asc nulls first")
    List<ListProgress> findReviewCandidates(@Param("userId") Long userId, @Param("listId") Long listId, @Param("now") OffsetDateTime now);

    @Query("select count(lp) from ListProgress lp "
            + "where lp.id.userId = :userId and lp.id.listId = :listId and lp.timesPracticed >= :threshold")
    long countMastered(@Param("userId") Long userId, @Param("listId") Long listId, @Param("threshold") int threshold);

    // Distinct lexeme across ALL lists for this user ("words known" is not
    // list-scoped even though progress itself is - doc/app-info/MVP.md).
    @Query("select count(distinct lp.lexeme.id) from ListProgress lp "
            + "where lp.id.userId = :userId and lp.timesPracticed >= :threshold")
    long countDistinctMasteredLexemes(@Param("userId") Long userId, @Param("threshold") int threshold);
}

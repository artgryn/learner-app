package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.ListProgress;
import com.artgr.learner.data.entity.ListProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ListProgressRepository extends JpaRepository<ListProgress, ListProgressId> {

    @Query("select count(lp) from ListProgress lp "
            + "where lp.id.userId = :userId and lp.id.listId = :listId and lp.timesPracticed >= :threshold")
    long countMastered(@Param("userId") Long userId, @Param("listId") Long listId, @Param("threshold") int threshold);

    // Distinct lexeme across ALL lists for this user ("words known" is not
    // list-scoped even though progress itself is - doc/app-info/MVP.md).
    @Query("select count(distinct lp.lexeme.id) from ListProgress lp "
            + "where lp.id.userId = :userId and lp.timesPracticed >= :threshold")
    long countDistinctMasteredLexemes(@Param("userId") Long userId, @Param("threshold") int threshold);
}

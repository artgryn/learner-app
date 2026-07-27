package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.LearningSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface LearningSessionRepository extends JpaRepository<LearningSession, Long> {

    @Query("select count(s) from LearningSession s where s.userId = :userId and s.listId = :listId")
    long countByUserIdAndListId(@Param("userId") Long userId, @Param("listId") Long listId);

    @Query("select max(coalesce(s.endedAt, s.startedAt)) from LearningSession s "
            + "where s.userId = :userId and s.listId = :listId")
    OffsetDateTime findLastActiveAt(@Param("userId") Long userId, @Param("listId") Long listId);

    // Truncated to LocalDate in the service, not here - letting the DB-native
    // function() return type round-trip through Spring Data's converter for
    // a generic List<LocalDate> was unreliable.
    @Query("select coalesce(s.endedAt, s.startedAt) from LearningSession s where s.userId = :userId")
    List<OffsetDateTime> findActiveTimestamps(@Param("userId") Long userId);
}

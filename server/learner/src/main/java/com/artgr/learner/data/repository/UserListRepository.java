package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.UserList;
import com.artgr.learner.data.entity.UserListId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserListRepository extends JpaRepository<UserList, UserListId> {

    // join fetch: open-in-view is off (application.yml), so list/targetLang/
    // baseLang must be loaded here, not lazily after the transaction closes.
    @Query("select ul from UserList ul "
            + "join fetch ul.list l "
            + "join fetch l.targetLang "
            + "join fetch ul.baseLang "
            + "where ul.id.userId = :userId")
    List<UserList> findByUserId(@Param("userId") Long userId);
}

package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.ListItem;
import com.artgr.learner.data.entity.ListItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListItemRepository extends JpaRepository<ListItem, ListItemId> {

    @Query("select count(li) from ListItem li where li.id.listId = :listId")
    long countByListId(@Param("listId") Long listId);

    @Query("select li from ListItem li where li.id.listId = :listId "
            + "order by li.position asc nulls last, li.lexeme.freqRank asc nulls last")
    List<ListItem> findByListIdOrderByPosition(@Param("listId") Long listId);
}

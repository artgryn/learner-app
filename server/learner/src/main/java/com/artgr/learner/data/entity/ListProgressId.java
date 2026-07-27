package com.artgr.learner.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ListProgressId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "list_id")
    private Long listId;

    @Column(name = "lexeme_id")
    private Long lexemeId;

    public ListProgressId() {}

    public ListProgressId(Long userId, Long listId, Long lexemeId) {
        this.userId = userId;
        this.listId = listId;
        this.lexemeId = lexemeId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getListId() { return listId; }
    public void setListId(Long listId) { this.listId = listId; }
    public Long getLexemeId() { return lexemeId; }
    public void setLexemeId(Long lexemeId) { this.lexemeId = lexemeId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListProgressId that)) return false;
        return Objects.equals(userId, that.userId)
                && Objects.equals(listId, that.listId)
                && Objects.equals(lexemeId, that.lexemeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, listId, lexemeId);
    }
}

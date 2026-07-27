package com.artgr.learner.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ListItemId implements Serializable {

    @Column(name = "list_id")
    private Long listId;

    @Column(name = "lexeme_id")
    private Long lexemeId;

    public ListItemId() {}

    public ListItemId(Long listId, Long lexemeId) {
        this.listId = listId;
        this.lexemeId = lexemeId;
    }

    public Long getListId() { return listId; }
    public void setListId(Long listId) { this.listId = listId; }
    public Long getLexemeId() { return lexemeId; }
    public void setLexemeId(Long lexemeId) { this.lexemeId = lexemeId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListItemId that)) return false;
        return Objects.equals(listId, that.listId) && Objects.equals(lexemeId, that.lexemeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listId, lexemeId);
    }
}

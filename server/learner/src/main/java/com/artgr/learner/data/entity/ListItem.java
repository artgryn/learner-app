package com.artgr.learner.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "list_item")
public class ListItem {

    @EmbeddedId
    private ListItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("listId")
    @JoinColumn(name = "list_id")
    private WordList list;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lexemeId")
    @JoinColumn(name = "lexeme_id")
    private Lexeme lexeme;

    @Column
    private Integer position;

    public ListItemId getId() { return id; }
    public void setId(ListItemId id) { this.id = id; }
    public WordList getList() { return list; }
    public void setList(WordList list) { this.list = list; }
    public Lexeme getLexeme() { return lexeme; }
    public void setLexeme(Lexeme lexeme) { this.lexeme = lexeme; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
}

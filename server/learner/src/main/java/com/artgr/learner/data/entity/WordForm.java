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
@Table(name = "word_form")
public class WordForm {

    @EmbeddedId
    private WordFormId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lexemeId")
    @JoinColumn(name = "lexeme_id")
    private Lexeme lexeme;

    @Column(nullable = false)
    private String form;

    public WordFormId getId() { return id; }
    public void setId(WordFormId id) { this.id = id; }
    public Lexeme getLexeme() { return lexeme; }
    public void setLexeme(Lexeme lexeme) { this.lexeme = lexeme; }
    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }
}

package com.artgr.learner.data.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

// Undirected pairwise link, stored once with lexemeA.id < lexemeB.id (enforced
// by a DB CHECK constraint, not here) and read both directions by callers.
@Entity
@Table(name = "translation")
public class Translation {

    @EmbeddedId
    private TranslationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lexemeA")
    @JoinColumn(name = "lexeme_a")
    private Lexeme lexemeA;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lexemeB")
    @JoinColumn(name = "lexeme_b")
    private Lexeme lexemeB;

    public TranslationId getId() { return id; }
    public void setId(TranslationId id) { this.id = id; }
    public Lexeme getLexemeA() { return lexemeA; }
    public void setLexemeA(Lexeme lexemeA) { this.lexemeA = lexemeA; }
    public Lexeme getLexemeB() { return lexemeB; }
    public void setLexemeB(Lexeme lexemeB) { this.lexemeB = lexemeB; }
}

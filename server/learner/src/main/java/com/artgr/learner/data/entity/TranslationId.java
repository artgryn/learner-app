package com.artgr.learner.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TranslationId implements Serializable {

    @Column(name = "lexeme_a")
    private Long lexemeA;

    @Column(name = "lexeme_b")
    private Long lexemeB;

    public TranslationId() {}

    public TranslationId(Long lexemeA, Long lexemeB) {
        this.lexemeA = lexemeA;
        this.lexemeB = lexemeB;
    }

    public Long getLexemeA() { return lexemeA; }
    public void setLexemeA(Long lexemeA) { this.lexemeA = lexemeA; }
    public Long getLexemeB() { return lexemeB; }
    public void setLexemeB(Long lexemeB) { this.lexemeB = lexemeB; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranslationId that)) return false;
        return Objects.equals(lexemeA, that.lexemeA) && Objects.equals(lexemeB, that.lexemeB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexemeA, lexemeB);
    }
}

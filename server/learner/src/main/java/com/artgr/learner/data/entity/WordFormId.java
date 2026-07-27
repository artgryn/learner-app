package com.artgr.learner.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WordFormId implements Serializable {

    @Column(name = "lexeme_id")
    private Long lexemeId;

    @Column(name = "form_type")
    private String formType;

    public WordFormId() {}

    public WordFormId(Long lexemeId, String formType) {
        this.lexemeId = lexemeId;
        this.formType = formType;
    }

    public Long getLexemeId() { return lexemeId; }
    public void setLexemeId(Long lexemeId) { this.lexemeId = lexemeId; }
    public String getFormType() { return formType; }
    public void setFormType(String formType) { this.formType = formType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WordFormId that)) return false;
        return Objects.equals(lexemeId, that.lexemeId) && Objects.equals(formType, that.formType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexemeId, formType);
    }
}

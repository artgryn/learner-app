package com.artgr.learner.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "lexeme", uniqueConstraints = @UniqueConstraint(columnNames = {"lang", "lemma", "pos"}))
public class Lexeme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lang", nullable = false, columnDefinition = "char(2)")
    private Language language;

    @Column(nullable = false)
    private String lemma;

    // Ready-to-display headword ("ett hus", "att gå", "to go"). Composed and
    // stored by INGESTION per language; the server reads this and never
    // composes articles/markers itself (doc/app-info/Data/lexeme.md).
    private String citation;

    @Column(nullable = false)
    private String pos;

    private String gender;

    @Column(name = "infl_class")
    private String inflClass;

    // Irreducible irregular parts only; empty {} for regular words (rules alone
    // produce every form). See doc/app-info/Data/lexeme.md.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, String> tema = new HashMap<>();

    private String note;

    @Column(name = "freq_rank")
    private Integer freqRank;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }
    public String getLemma() { return lemma; }
    public void setLemma(String lemma) { this.lemma = lemma; }
    public String getCitation() { return citation; }
    public void setCitation(String citation) { this.citation = citation; }
    public String getPos() { return pos; }
    public void setPos(String pos) { this.pos = pos; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getInflClass() { return inflClass; }
    public void setInflClass(String inflClass) { this.inflClass = inflClass; }
    public Map<String, String> getTema() { return tema; }
    public void setTema(Map<String, String> tema) { this.tema = tema; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Integer getFreqRank() { return freqRank; }
    public void setFreqRank(Integer freqRank) { this.freqRank = freqRank; }
}

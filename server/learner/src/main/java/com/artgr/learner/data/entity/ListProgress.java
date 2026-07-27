package com.artgr.learner.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

// Per-(user, list, lexeme) practice counter. (user_id, list_id) FKs to
// UserList (the enrollment) at the DB level (01_schema.sql) but is kept as
// plain scalars here rather than a JPA association, to avoid a nested
// composite-embeddable-id for a relation nothing here needs to navigate yet.
@Entity
@Table(name = "list_progress")
public class ListProgress {

    @EmbeddedId
    private ListProgressId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lexemeId")
    @JoinColumn(name = "lexeme_id")
    private Lexeme lexeme;

    @Column(name = "times_practiced", nullable = false)
    private int timesPracticed;

    @Column(nullable = false)
    private int correct;

    @Column(nullable = false)
    private int wrong;

    private OffsetDateTime due;

    public ListProgressId getId() { return id; }
    public void setId(ListProgressId id) { this.id = id; }
    public Lexeme getLexeme() { return lexeme; }
    public void setLexeme(Lexeme lexeme) { this.lexeme = lexeme; }
    public int getTimesPracticed() { return timesPracticed; }
    public void setTimesPracticed(int timesPracticed) { this.timesPracticed = timesPracticed; }
    public int getCorrect() { return correct; }
    public void setCorrect(int correct) { this.correct = correct; }
    public int getWrong() { return wrong; }
    public void setWrong(int wrong) { this.wrong = wrong; }
    public OffsetDateTime getDue() { return due; }
    public void setDue(OffsetDateTime due) { this.due = due; }
}

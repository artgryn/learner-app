package com.artgr.learner.data.entity;

import com.artgr.learner.data.enums.ExerciseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

// Append-only log, one row per answered exercise, never updated. Kept
// denormalized (individual FKs, not re-parented to UserList) for write speed.
@Entity
@Table(name = "attempt")
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private WordList list;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lexeme_id", nullable = false)
    private Lexeme lexeme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private LearningSession session;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "exercise_type", nullable = false)
    private ExerciseType exerciseType;

    @Column(name = "form_type")
    private String formType;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "elapsed_ms")
    private Integer elapsedMs;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public WordList getList() { return list; }
    public void setList(WordList list) { this.list = list; }
    public Lexeme getLexeme() { return lexeme; }
    public void setLexeme(Lexeme lexeme) { this.lexeme = lexeme; }
    public LearningSession getSession() { return session; }
    public void setSession(LearningSession session) { this.session = session; }
    public ExerciseType getExerciseType() { return exerciseType; }
    public void setExerciseType(ExerciseType exerciseType) { this.exerciseType = exerciseType; }
    public String getFormType() { return formType; }
    public void setFormType(String formType) { this.formType = formType; }
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public Integer getElapsedMs() { return elapsedMs; }
    public void setElapsedMs(Integer elapsedMs) { this.elapsedMs = elapsedMs; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

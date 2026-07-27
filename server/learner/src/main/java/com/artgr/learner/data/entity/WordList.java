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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;

// Table is named "list"; entity is WordList to avoid clashing with java.util.List.
@Entity
@Table(name = "list")
public class WordList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_lang", nullable = false, columnDefinition = "char(2)")
    private Language targetLang;

    // NULL = curated/global list; set = user- or AI-generated (post-MVP).
    // No FK constraint in 01_schema.sql - column is reserved, not yet enforced.
    @Column(name = "user_id")
    private Long userId;

    // NULL = all exercise types permitted for this list.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "allowed_exercises", columnDefinition = "exercise_type[]")
    private Set<ExerciseType> allowedExercises;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Language getTargetLang() { return targetLang; }
    public void setTargetLang(Language targetLang) { this.targetLang = targetLang; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Set<ExerciseType> getAllowedExercises() { return allowedExercises; }
    public void setAllowedExercises(Set<ExerciseType> allowedExercises) { this.allowedExercises = allowedExercises; }
}

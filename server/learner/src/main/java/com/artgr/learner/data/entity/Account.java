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
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // Display name; set via PATCH /me (init_account calls this same endpoint
    // post-registration - there is no separate init endpoint).
    private String name;

    // App UI language. Nullable at the DB level despite the 'en' default
    // (see 01_schema.sql).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ui_lang", columnDefinition = "char(2)")
    private Language uiLang;

    // Default/filter learning pair (catalog filter + enroll pre-fill) - NOT the
    // per-enrollment source of truth, that's UserList.baseLang.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learn_base_lang", columnDefinition = "char(2)")
    private Language learnBaseLang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learn_target_lang", columnDefinition = "char(2)")
    private Language learnTargetLang;

    // 'free' | 'paid' - billing tier, not user-editable, no billing logic yet.
    @Column(nullable = false)
    private String status = "free";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Language getUiLang() { return uiLang; }
    public void setUiLang(Language uiLang) { this.uiLang = uiLang; }
    public Language getLearnBaseLang() { return learnBaseLang; }
    public void setLearnBaseLang(Language learnBaseLang) { this.learnBaseLang = learnBaseLang; }
    public Language getLearnTargetLang() { return learnTargetLang; }
    public void setLearnTargetLang(Language learnTargetLang) { this.learnTargetLang = learnTargetLang; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}

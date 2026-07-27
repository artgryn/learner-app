package com.artgr.learner.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

// Enrollment: assigning a list to a user = one row here, a reference, not a
// copy of the list's words. Parent of ListProgress and LearningSession.
@Entity
@Table(name = "user_list")
public class UserList {

    @EmbeddedId
    private UserListId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("listId")
    @JoinColumn(name = "list_id")
    private WordList list;

    // Taught-from language for this enrollment (per-enrollment, not per-account).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_lang", nullable = false, columnDefinition = "char(2)")
    private Language baseLang;

    @Column(nullable = false)
    private String status = "active";

    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    public UserListId getId() { return id; }
    public void setId(UserListId id) { this.id = id; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public WordList getList() { return list; }
    public void setList(WordList list) { this.list = list; }
    public Language getBaseLang() { return baseLang; }
    public void setBaseLang(Language baseLang) { this.baseLang = baseLang; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; }
}

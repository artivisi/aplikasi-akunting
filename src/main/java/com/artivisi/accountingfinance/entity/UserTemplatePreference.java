package com.artivisi.accountingfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_template_preferences", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_user", "id_journal_template"})
})
@Getter
@Setter
@NoArgsConstructor
public class UserTemplatePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @NotNull(message = "Journal template is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_journal_template", nullable = false)
    private JournalTemplate journalTemplate;

    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = false;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Min(value = 0, message = "Use count cannot be negative")
    @Column(name = "use_count", nullable = false)
    private Integer useCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UserTemplatePreference(User user, JournalTemplate journalTemplate) {
        this.user = user;
        this.journalTemplate = journalTemplate;
    }

    public void recordUsage() {
        this.useCount = this.useCount + 1;
        this.lastUsedAt = LocalDateTime.now();
    }

    public void toggleFavorite() {
        this.isFavorite = !this.isFavorite;
    }
}

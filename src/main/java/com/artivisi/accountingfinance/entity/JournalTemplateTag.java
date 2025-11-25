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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "journal_template_tags", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_journal_template", "tag"})
})
@Getter
@Setter
@NoArgsConstructor
public class JournalTemplateTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Journal template is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_journal_template", nullable = false)
    private JournalTemplate journalTemplate;

    @NotBlank(message = "Tag is required")
    @Size(max = 50, message = "Tag must not exceed 50 characters")
    @Column(name = "tag", nullable = false, length = 50)
    private String tag;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public JournalTemplateTag(JournalTemplate journalTemplate, String tag) {
        this.journalTemplate = journalTemplate;
        this.tag = tag;
    }
}

package com.artivisi.accountingfinance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tags", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_tag_type", "code"})
})
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Tag extends BaseEntity {

    @JsonIgnore
    @NotNull(message = "Tipe label wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tag_type", nullable = false)
    private TagType tagType;

    @NotBlank(message = "Kode label wajib diisi")
    @Size(max = 20, message = "Kode label maksimal 20 karakter")
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @NotBlank(message = "Nama label wajib diisi")
    @Size(max = 100, message = "Nama label maksimal 100 karakter")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 255, message = "Deskripsi maksimal 255 karakter")
    @Column(name = "description")
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}

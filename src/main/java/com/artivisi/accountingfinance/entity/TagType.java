package com.artivisi.accountingfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "tag_types")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class TagType extends BaseEntity {

    @NotBlank(message = "Kode tipe label wajib diisi")
    @Size(max = 20, message = "Kode tipe label maksimal 20 karakter")
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @NotBlank(message = "Nama tipe label wajib diisi")
    @Size(max = 100, message = "Nama tipe label maksimal 100 karakter")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 255, message = "Deskripsi maksimal 255 karakter")
    @Column(name = "description")
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "tagType", fetch = FetchType.LAZY)
    private List<Tag> tags = new ArrayList<>();

    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }
}

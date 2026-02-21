package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.TagType;
import com.artivisi.accountingfinance.repository.TagRepository;
import com.artivisi.accountingfinance.repository.TagTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagTypeService {

    private final TagTypeRepository tagTypeRepository;
    private final TagRepository tagRepository;

    public TagType create(TagType tagType) {
        validateUniqueCode(tagType.getCode(), null);
        return tagTypeRepository.save(tagType);
    }

    public TagType update(UUID id, TagType updated) {
        TagType existing = tagTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipe label tidak ditemukan: " + id));

        validateUniqueCode(updated.getCode(), id);

        existing.setCode(updated.getCode());
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setActive(updated.isActive());

        return tagTypeRepository.save(existing);
    }

    public void delete(UUID id) {
        TagType tagType = tagTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipe label tidak ditemukan: " + id));

        long tagCount = tagRepository.countByTagTypeId(id);
        if (tagCount > 0) {
            throw new IllegalStateException("Tipe label tidak dapat dihapus karena masih memiliki " + tagCount + " label");
        }

        tagType.softDelete();
        tagTypeRepository.save(tagType);
        log.info("Soft-deleted tag type: {}", tagType.getCode());
    }

    @Transactional(readOnly = true)
    public Optional<TagType> findById(UUID id) {
        return tagTypeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<TagType> findAllActive() {
        return tagTypeRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Page<TagType> findBySearch(String search, Pageable pageable) {
        return tagTypeRepository.findBySearch(search, pageable);
    }

    private void validateUniqueCode(String code, UUID excludeId) {
        if (excludeId == null) {
            if (tagTypeRepository.existsByCode(code)) {
                throw new IllegalArgumentException("Kode tipe label sudah digunakan: " + code);
            }
        } else {
            if (tagTypeRepository.existsByCodeAndIdNot(code, excludeId)) {
                throw new IllegalArgumentException("Kode tipe label sudah digunakan: " + code);
            }
        }
    }
}

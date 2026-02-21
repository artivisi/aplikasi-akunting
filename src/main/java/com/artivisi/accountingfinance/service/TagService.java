package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.Tag;
import com.artivisi.accountingfinance.entity.TagType;
import com.artivisi.accountingfinance.repository.TagRepository;
import com.artivisi.accountingfinance.repository.TransactionTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final TransactionTagRepository transactionTagRepository;

    public Tag create(Tag tag) {
        validateUniqueCode(tag.getTagType().getId(), tag.getCode(), null);
        return tagRepository.save(tag);
    }

    public Tag update(UUID id, Tag updated) {
        Tag existing = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Label tidak ditemukan: " + id));

        validateUniqueCode(existing.getTagType().getId(), updated.getCode(), id);

        existing.setCode(updated.getCode());
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setActive(updated.isActive());

        return tagRepository.save(existing);
    }

    public void delete(UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Label tidak ditemukan: " + id));

        long usageCount = transactionTagRepository.countByTagId(id);
        if (usageCount > 0) {
            throw new IllegalStateException("Label tidak dapat dihapus karena digunakan oleh " + usageCount + " transaksi");
        }

        tag.softDelete();
        tagRepository.save(tag);
        log.info("Soft-deleted tag: {} (type: {})", tag.getCode(), tag.getTagType().getCode());
    }

    @Transactional(readOnly = true)
    public Optional<Tag> findById(UUID id) {
        return tagRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Tag> findByTagType(UUID tagTypeId) {
        return tagRepository.findByTagTypeIdOrderByName(tagTypeId);
    }

    @Transactional(readOnly = true)
    public Page<Tag> findByTagTypeAndSearch(UUID tagTypeId, String search, Pageable pageable) {
        return tagRepository.findByTagTypeIdAndSearch(tagTypeId, search, pageable);
    }

    @Transactional(readOnly = true)
    public List<Tag> findAllActive() {
        return tagRepository.findAllActiveOrdered();
    }

    @Transactional(readOnly = true)
    public Map<TagType, List<Tag>> findAllActiveGroupedByType() {
        List<Tag> tags = tagRepository.findAllActiveOrdered();
        return tags.stream()
                .collect(Collectors.groupingBy(Tag::getTagType, LinkedHashMap::new, Collectors.toList()));
    }

    private void validateUniqueCode(UUID tagTypeId, String code, UUID excludeId) {
        if (excludeId == null) {
            if (tagRepository.existsByTagTypeIdAndCode(tagTypeId, code)) {
                throw new IllegalArgumentException("Kode label sudah digunakan dalam tipe ini: " + code);
            }
        } else {
            if (tagRepository.existsByTagTypeIdAndCodeAndIdNot(tagTypeId, code, excludeId)) {
                throw new IllegalArgumentException("Kode label sudah digunakan dalam tipe ini: " + code);
            }
        }
    }
}

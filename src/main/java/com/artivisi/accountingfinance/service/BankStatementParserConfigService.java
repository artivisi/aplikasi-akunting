package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.BankStatementParserConfig;
import com.artivisi.accountingfinance.enums.BankStatementParserType;
import com.artivisi.accountingfinance.repository.BankStatementParserConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankStatementParserConfigService {

    private final BankStatementParserConfigRepository repository;

    public List<BankStatementParserConfig> findAll() {
        return repository.findAllByOrderByConfigNameAsc();
    }

    public List<BankStatementParserConfig> findActive() {
        return repository.findByActiveTrueOrderByConfigNameAsc();
    }

    public BankStatementParserConfig findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parser config not found with id: " + id));
    }

    public List<BankStatementParserConfig> findByBankType(BankStatementParserType bankType) {
        return repository.findByBankTypeAndActiveTrue(bankType);
    }

    @Transactional
    public BankStatementParserConfig create(BankStatementParserConfig config) {
        if (repository.existsByConfigName(config.getConfigName())) {
            throw new IllegalArgumentException("Nama konfigurasi sudah digunakan: " + config.getConfigName());
        }
        config.setIsSystem(false);
        return repository.save(config);
    }

    @Transactional
    public BankStatementParserConfig update(UUID id, BankStatementParserConfig updated) {
        BankStatementParserConfig existing = findById(id);

        if (!existing.getConfigName().equals(updated.getConfigName())
                && repository.existsByConfigName(updated.getConfigName())) {
            throw new IllegalArgumentException("Nama konfigurasi sudah digunakan: " + updated.getConfigName());
        }

        existing.setBankType(updated.getBankType());
        existing.setConfigName(updated.getConfigName());
        existing.setDescription(updated.getDescription());
        existing.setDateColumn(updated.getDateColumn());
        existing.setDescriptionColumn(updated.getDescriptionColumn());
        existing.setDebitColumn(updated.getDebitColumn());
        existing.setCreditColumn(updated.getCreditColumn());
        existing.setBalanceColumn(updated.getBalanceColumn());
        existing.setDateFormat(updated.getDateFormat());
        existing.setDelimiter(updated.getDelimiter());
        existing.setSkipHeaderRows(updated.getSkipHeaderRows());
        existing.setEncoding(updated.getEncoding());
        existing.setDecimalSeparator(updated.getDecimalSeparator());
        existing.setThousandSeparator(updated.getThousandSeparator());

        return repository.save(existing);
    }

    @Transactional
    public void deactivate(UUID id) {
        BankStatementParserConfig config = findById(id);
        config.setActive(false);
        repository.save(config);
    }

    @Transactional
    public void activate(UUID id) {
        BankStatementParserConfig config = findById(id);
        config.setActive(true);
        repository.save(config);
    }

    @Transactional
    public void delete(UUID id) {
        BankStatementParserConfig config = findById(id);
        if (config.isSystemConfig()) {
            throw new IllegalArgumentException("Konfigurasi sistem tidak dapat dihapus, nonaktifkan saja");
        }
        config.softDelete();
        repository.save(config);
    }
}

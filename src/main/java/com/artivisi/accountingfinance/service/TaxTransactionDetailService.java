package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.Client;
import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.entity.TaxTransactionDetail;
import com.artivisi.accountingfinance.entity.Transaction;
import com.artivisi.accountingfinance.enums.TaxType;
import com.artivisi.accountingfinance.enums.TemplateCategory;
import com.artivisi.accountingfinance.repository.TaxTransactionDetailRepository;
import com.artivisi.accountingfinance.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TaxTransactionDetailService {

    private static final String DETAIL_NOT_FOUND = "Tax detail tidak ditemukan: ";
    private static final String TRANSACTION_NOT_FOUND = "Transaksi tidak ditemukan: ";

    // Tax account codes for auto-detection
    private static final String ACCOUNT_HUTANG_PPN = "2.1.03";
    private static final String ACCOUNT_PPN_MASUKAN = "1.1.25";
    private static final String ACCOUNT_KREDIT_PPH_23 = "1.1.26";
    private static final String ACCOUNT_HUTANG_PPH_21 = "2.1.20";
    private static final String ACCOUNT_HUTANG_PPH_23 = "2.1.21";
    private static final String ACCOUNT_HUTANG_PPH_42 = "2.1.22";

    private final TaxTransactionDetailRepository taxDetailRepository;
    private final TransactionRepository transactionRepository;
    private final TaxTransactionDetailService self;

    public TaxTransactionDetailService(
            TaxTransactionDetailRepository taxDetailRepository,
            TransactionRepository transactionRepository,
            @Lazy TaxTransactionDetailService self) {
        this.taxDetailRepository = taxDetailRepository;
        this.transactionRepository = transactionRepository;
        this.self = self;
    }

    public List<TaxTransactionDetail> findByTransactionId(UUID transactionId) {
        return taxDetailRepository.findAllByTransactionIdOrderByTaxTypeAsc(transactionId);
    }

    public TaxTransactionDetail findById(UUID id) {
        return taxDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DETAIL_NOT_FOUND + id));
    }

    @Transactional
    public TaxTransactionDetail save(UUID transactionId, TaxTransactionDetail detail) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(TRANSACTION_NOT_FOUND + transactionId));

        validate(detail, null);

        detail.setTransaction(transaction);
        return taxDetailRepository.save(detail);
    }

    @Transactional
    public TaxTransactionDetail update(UUID detailId, TaxTransactionDetail updated) {
        TaxTransactionDetail existing = taxDetailRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException(DETAIL_NOT_FOUND + detailId));

        validate(updated, detailId);

        // Merge fields
        existing.setTaxType(updated.getTaxType());
        existing.setFakturNumber(updated.getFakturNumber());
        existing.setFakturDate(updated.getFakturDate());
        existing.setTransactionCode(updated.getTransactionCode());
        existing.setDpp(updated.getDpp());
        existing.setPpn(updated.getPpn());
        existing.setPpnbm(updated.getPpnbm());
        existing.setBupotNumber(updated.getBupotNumber());
        existing.setTaxObjectCode(updated.getTaxObjectCode());
        existing.setGrossAmount(updated.getGrossAmount());
        existing.setTaxRate(updated.getTaxRate());
        existing.setTaxAmount(updated.getTaxAmount());
        existing.setCounterpartyNpwp(updated.getCounterpartyNpwp());
        existing.setCounterpartyNitku(updated.getCounterpartyNitku());
        existing.setCounterpartyNik(updated.getCounterpartyNik());
        existing.setCounterpartyIdType(updated.getCounterpartyIdType());
        existing.setCounterpartyName(updated.getCounterpartyName());
        existing.setCounterpartyAddress(updated.getCounterpartyAddress());

        return taxDetailRepository.save(existing);
    }

    @Transactional
    public void delete(UUID detailId) {
        TaxTransactionDetail detail = taxDetailRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException(DETAIL_NOT_FOUND + detailId));
        taxDetailRepository.delete(detail);
    }

    @Transactional
    public int autoPopulateFromTransaction(Transaction transaction) {
        List<TaxTransactionDetail> existing = findByTransactionId(transaction.getId());
        if (!existing.isEmpty()) {
            log.debug("Transaction {} already has {} tax details, skipping auto-populate",
                    transaction.getId(), existing.size());
            return 0;
        }

        List<TaxDetailSuggestion> suggestions = suggestFromTransaction(transaction);
        if (suggestions.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (TaxDetailSuggestion suggestion : suggestions) {
            if (suggestion.name() == null || suggestion.name().isBlank()) {
                log.debug("Skipping auto-populate for {} — no counterparty name available",
                        suggestion.taxType());
                continue;
            }

            TaxTransactionDetail detail = new TaxTransactionDetail();
            detail.setTaxType(suggestion.taxType());
            detail.setTransactionCode(suggestion.transactionCode());
            detail.setDpp(suggestion.dpp());
            detail.setPpn(suggestion.ppn());
            detail.setGrossAmount(suggestion.grossAmount());
            detail.setTaxRate(suggestion.taxRate());
            detail.setTaxAmount(suggestion.taxAmount());
            detail.setCounterpartyNpwp(suggestion.npwp());
            detail.setCounterpartyNitku(suggestion.nitku());
            detail.setCounterpartyNik(suggestion.nik());
            detail.setCounterpartyIdType(suggestion.idType());
            detail.setCounterpartyName(suggestion.name());
            detail.setCounterpartyAddress(suggestion.address());

            self.save(transaction.getId(), detail);
            count++;
        }

        log.info("Auto-populated {} tax details for transaction {}", count, transaction.getId());
        return count;
    }

    public Set<UUID> findTransactionIdsWithDetails(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        return taxDetailRepository.findTransactionIdsWithDetails(ids);
    }

    /**
     * Analyze a transaction's template name and journal entries to suggest tax detail values.
     */
    public List<TaxDetailSuggestion> suggestFromTransaction(Transaction transaction) {
        List<TaxDetailSuggestion> suggestions = new ArrayList<>();
        String templateName = transaction.getJournalTemplate() != null
                ? transaction.getJournalTemplate().getTemplateName() : "";
        TemplateCategory category = transaction.getJournalTemplate() != null
                ? transaction.getJournalTemplate().getCategory() : null;

        // Extract counterparty info from project's client
        Client client = (transaction.getProject() != null) ? transaction.getProject().getClient() : null;

        // Detect PPN
        if (templateName.toUpperCase().contains("PPN")) {
            TaxType ppnType = (category == TemplateCategory.INCOME || category == TemplateCategory.RECEIPT)
                    ? TaxType.PPN_KELUARAN : TaxType.PPN_MASUKAN;

            BigDecimal dpp = BigDecimal.ZERO;
            BigDecimal ppn = BigDecimal.ZERO;
            for (JournalEntry entry : transaction.getJournalEntries()) {
                String accountCode = entry.getAccount() != null ? entry.getAccount().getAccountCode() : "";
                if (ACCOUNT_HUTANG_PPN.equals(accountCode)) {
                    ppn = entry.getCreditAmount() != null ? entry.getCreditAmount() : BigDecimal.ZERO;
                } else if (ACCOUNT_PPN_MASUKAN.equals(accountCode)) {
                    ppn = entry.getDebitAmount() != null ? entry.getDebitAmount() : BigDecimal.ZERO;
                }
            }
            // DPP = transaction amount (Harga Jual) for PPN templates
            dpp = transaction.getAmount();

            String transactionCode = templateName.toUpperCase().contains("BUMN")
                    || templateName.toUpperCase().contains("FP 03") ? "03" : "01";

            suggestions.add(new TaxDetailSuggestion(
                    ppnType, transactionCode,
                    dpp, ppn, null, null, null,
                    client != null ? client.getNpwp() : null,
                    client != null ? client.getNitku() : null,
                    client != null ? client.getNik() : null,
                    client != null ? client.getIdType() : "TIN",
                    client != null ? client.getName() : null,
                    client != null ? client.getAddress() : null
            ));
        }

        // Detect PPh 23
        if (templateName.toUpperCase().contains("PPH 23") || templateName.toUpperCase().contains("PPH23")) {
            BigDecimal grossAmount = transaction.getAmount();
            BigDecimal taxRate = new BigDecimal("2.00");
            BigDecimal taxAmount = BigDecimal.ZERO;

            for (JournalEntry entry : transaction.getJournalEntries()) {
                String accountCode = entry.getAccount() != null ? entry.getAccount().getAccountCode() : "";
                if (ACCOUNT_KREDIT_PPH_23.equals(accountCode)) {
                    taxAmount = entry.getDebitAmount() != null ? entry.getDebitAmount() : BigDecimal.ZERO;
                } else if (ACCOUNT_HUTANG_PPH_23.equals(accountCode)) {
                    taxAmount = entry.getCreditAmount() != null ? entry.getCreditAmount() : BigDecimal.ZERO;
                }
            }

            suggestions.add(new TaxDetailSuggestion(
                    TaxType.PPH_23, null,
                    null, null,
                    grossAmount, taxRate, taxAmount,
                    client != null ? client.getNpwp() : null,
                    client != null ? client.getNitku() : null,
                    client != null ? client.getNik() : null,
                    client != null ? client.getIdType() : "TIN",
                    client != null ? client.getName() : null,
                    client != null ? client.getAddress() : null
            ));
        }

        // Detect PPh 4(2)
        if (templateName.toUpperCase().contains("PPH 4(2)") || templateName.toUpperCase().contains("PPH 42")
                || templateName.toUpperCase().contains("PPH4(2)")) {
            BigDecimal grossAmount = transaction.getAmount();
            BigDecimal taxRate = new BigDecimal("10.00");
            BigDecimal taxAmount = BigDecimal.ZERO;

            for (JournalEntry entry : transaction.getJournalEntries()) {
                String accountCode = entry.getAccount() != null ? entry.getAccount().getAccountCode() : "";
                if (ACCOUNT_HUTANG_PPH_42.equals(accountCode)) {
                    taxAmount = entry.getCreditAmount() != null ? entry.getCreditAmount() : BigDecimal.ZERO;
                }
            }

            suggestions.add(new TaxDetailSuggestion(
                    TaxType.PPH_42, null,
                    null, null,
                    grossAmount, taxRate, taxAmount,
                    client != null ? client.getNpwp() : null,
                    client != null ? client.getNitku() : null,
                    client != null ? client.getNik() : null,
                    client != null ? client.getIdType() : "TIN",
                    client != null ? client.getName() : null,
                    client != null ? client.getAddress() : null
            ));
        }

        // Detect PPh 21
        if (templateName.toUpperCase().contains("PPH 21") || templateName.toUpperCase().contains("PPH21")
                || templateName.toUpperCase().contains("GAJI")) {
            BigDecimal taxAmount = BigDecimal.ZERO;
            for (JournalEntry entry : transaction.getJournalEntries()) {
                String accountCode = entry.getAccount() != null ? entry.getAccount().getAccountCode() : "";
                if (ACCOUNT_HUTANG_PPH_21.equals(accountCode)) {
                    taxAmount = entry.getCreditAmount() != null ? entry.getCreditAmount() : BigDecimal.ZERO;
                }
            }
            if (taxAmount.compareTo(BigDecimal.ZERO) > 0) {
                suggestions.add(new TaxDetailSuggestion(
                        TaxType.PPH_21, null,
                        null, null,
                        transaction.getAmount(), null, taxAmount,
                        client != null ? client.getNpwp() : null,
                        client != null ? client.getNitku() : null,
                        client != null ? client.getNik() : null,
                        client != null ? client.getIdType() : "TIN",
                        client != null ? client.getName() : null,
                        client != null ? client.getAddress() : null
                ));
            }
        }

        return suggestions;
    }

    private void validate(TaxTransactionDetail detail, UUID existingId) {
        if (detail.getTaxType() == null) {
            throw new IllegalArgumentException("Jenis pajak (taxType) wajib diisi");
        }

        if (detail.getCounterpartyName() == null || detail.getCounterpartyName().isBlank()) {
            throw new IllegalArgumentException("Nama lawan transaksi wajib diisi");
        }

        if (detail.isEFaktur()) {
            if (detail.getDpp() == null) {
                throw new IllegalArgumentException("DPP wajib diisi untuk e-Faktur");
            }
            if (detail.getPpn() == null) {
                throw new IllegalArgumentException("PPN wajib diisi untuk e-Faktur");
            }
            if (detail.getFakturNumber() != null && !detail.getFakturNumber().isBlank()) {
                boolean duplicate = (existingId == null)
                        ? taxDetailRepository.existsByFakturNumber(detail.getFakturNumber())
                        : taxDetailRepository.existsByFakturNumberAndIdNot(detail.getFakturNumber(), existingId);
                if (duplicate) {
                    throw new IllegalArgumentException("Nomor faktur sudah digunakan: " + detail.getFakturNumber());
                }
            }
        }

        if (detail.isEBupot()) {
            if (detail.getGrossAmount() == null) {
                throw new IllegalArgumentException("Jumlah bruto wajib diisi untuk e-Bupot");
            }
            if (detail.getTaxRate() == null) {
                throw new IllegalArgumentException("Tarif pajak wajib diisi untuk e-Bupot");
            }
            if (detail.getTaxAmount() == null) {
                throw new IllegalArgumentException("Jumlah pajak wajib diisi untuk e-Bupot");
            }
            if (detail.getBupotNumber() != null && !detail.getBupotNumber().isBlank()) {
                boolean duplicate = (existingId == null)
                        ? taxDetailRepository.existsByBupotNumber(detail.getBupotNumber())
                        : taxDetailRepository.existsByBupotNumberAndIdNot(detail.getBupotNumber(), existingId);
                if (duplicate) {
                    throw new IllegalArgumentException("Nomor bukti potong sudah digunakan: " + detail.getBupotNumber());
                }
            }
        }

        // NPWP format validation
        if (detail.getCounterpartyNpwp() != null && !detail.getCounterpartyNpwp().isBlank()) {
            String npwp = detail.getCounterpartyNpwp().replaceAll("[^0-9]", "");
            if (npwp.length() != 15 && npwp.length() != 16) {
                throw new IllegalArgumentException("NPWP harus 15 atau 16 digit");
            }
        }
    }

    public record TaxDetailSuggestion(
            TaxType taxType, String transactionCode,
            BigDecimal dpp, BigDecimal ppn,
            BigDecimal grossAmount, BigDecimal taxRate, BigDecimal taxAmount,
            String npwp, String nitku, String nik, String idType,
            String name, String address
    ) {}
}

package com.artivisi.accountingfinance.entity;

import com.artivisi.accountingfinance.enums.TaxType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void merchantMapping_shouldMatchExact() {
        MerchantMapping mapping = new MerchantMapping();
        mapping.setMerchantPattern("TOKOPEDIA");
        mapping.setMatchType(MerchantMapping.MatchType.EXACT);

        assertThat(mapping.matches("TOKOPEDIA")).isTrue();
        assertThat(mapping.matches("tokopedia")).isTrue();
        assertThat(mapping.matches("TOKOPEDIA SELLER")).isFalse();
    }

    @Test
    void merchantMapping_shouldMatchContains() {
        MerchantMapping mapping = new MerchantMapping();
        mapping.setMerchantPattern("GRAB");
        mapping.setMatchType(MerchantMapping.MatchType.CONTAINS);

        assertThat(mapping.matches("GRABFOOD")).isTrue();
        assertThat(mapping.matches("grabcar")).isTrue();
        assertThat(mapping.matches("GOJEK")).isFalse();
    }

    @Test
    void merchantMapping_shouldMatchRegex() {
        MerchantMapping mapping = new MerchantMapping();
        mapping.setMerchantPattern("GOJEK.*FOOD");
        mapping.setMatchType(MerchantMapping.MatchType.REGEX);

        assertThat(mapping.matches("GOJEK FOOD")).isTrue();
        assertThat(mapping.matches("GOJEK_FOOD")).isTrue();
        assertThat(mapping.matches("GRABFOOD")).isFalse();
    }

    @Test
    void merchantMapping_shouldReturnFalseForNullInput() {
        MerchantMapping mapping = new MerchantMapping();
        mapping.setMerchantPattern("TEST");
        mapping.setMatchType(MerchantMapping.MatchType.EXACT);

        assertThat(mapping.matches(null)).isFalse();
    }

    @Test
    void merchantMapping_shouldReturnFalseForNullPattern() {
        MerchantMapping mapping = new MerchantMapping();
        mapping.setMatchType(MerchantMapping.MatchType.EXACT);

        assertThat(mapping.matches("TEST")).isFalse();
    }

    @Test
    void merchantMapping_shouldIncrementMatchCount() {
        MerchantMapping mapping = new MerchantMapping();
        mapping.setMatchCount(5);

        mapping.incrementMatchCount();

        assertThat(mapping.getMatchCount()).isEqualTo(6);
        assertThat(mapping.getLastUsedAt()).isNotNull();
    }

    @Test
    void userTemplatePreference_shouldRecordUsage() {
        UserTemplatePreference pref = new UserTemplatePreference();
        pref.setUseCount(10);

        pref.recordUsage();

        assertThat(pref.getUseCount()).isEqualTo(11);
        assertThat(pref.getLastUsedAt()).isNotNull();
    }

    @Test
    void userTemplatePreference_shouldToggleFavorite() {
        UserTemplatePreference pref = new UserTemplatePreference();
        pref.setIsFavorite(false);

        pref.toggleFavorite();
        assertThat(pref.getIsFavorite()).isTrue();

        pref.toggleFavorite();
        assertThat(pref.getIsFavorite()).isFalse();
    }

    @Test
    void userTemplatePreference_constructorWithUserAndTemplate() {
        User user = new User();
        JournalTemplate template = new JournalTemplate();

        UserTemplatePreference pref = new UserTemplatePreference(user, template);

        assertThat(pref.getUser()).isEqualTo(user);
        assertThat(pref.getJournalTemplate()).isEqualTo(template);
    }

    @Test
    void transactionVariable_shouldStoreValues() {
        TransactionVariable txVar = new TransactionVariable();
        txVar.setVariableName("tax_rate");
        txVar.setVariableValue(BigDecimal.valueOf(0.11));

        assertThat(txVar.getVariableName()).isEqualTo("tax_rate");
        assertThat(txVar.getVariableValue()).isEqualTo(BigDecimal.valueOf(0.11));
    }

    @Test
    void transactionVariable_constructorWithValues() {
        TransactionVariable txVar = new TransactionVariable("discount", BigDecimal.valueOf(50000));

        assertThat(txVar.getVariableName()).isEqualTo("discount");
        assertThat(txVar.getVariableValue()).isEqualTo(BigDecimal.valueOf(50000));
    }

    @Test
    void taxDeadlineCompletion_shouldStoreValues() {
        TaxDeadlineCompletion completion = new TaxDeadlineCompletion();
        TaxDeadline deadline = new TaxDeadline();
        completion.setTaxDeadline(deadline);
        completion.setCompletedDate(LocalDate.of(2024, 1, 15));
        completion.setCompletedBy("admin");
        completion.setNotes("Completed on time");
        completion.setYear(2024);
        completion.setMonth(1);
        completion.setReferenceNumber("REF-001");

        assertThat(completion.getTaxDeadline()).isEqualTo(deadline);
        assertThat(completion.getCompletedDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(completion.getCompletedBy()).isEqualTo("admin");
        assertThat(completion.getNotes()).isEqualTo("Completed on time");
        assertThat(completion.getYear()).isEqualTo(2024);
        assertThat(completion.getMonth()).isEqualTo(1);
    }

    @Test
    void taxDeadlineCompletion_shouldGetPeriodName() {
        TaxDeadlineCompletion completion = new TaxDeadlineCompletion();
        completion.setYear(2024);
        completion.setMonth(3);

        assertThat(completion.getPeriodName()).isEqualTo("2024-03");
    }

    @Test
    void taxDeadlineCompletion_shouldGetPeriodDisplayName() {
        TaxDeadlineCompletion completion = new TaxDeadlineCompletion();
        completion.setYear(2024);
        completion.setMonth(3);

        assertThat(completion.getPeriodDisplayName()).isEqualTo("Maret 2024");
    }

    @Test
    void employeeSalaryComponent_shouldStoreValues() {
        EmployeeSalaryComponent component = new EmployeeSalaryComponent();
        Employee employee = new Employee();
        SalaryComponent salaryComponent = new SalaryComponent();
        component.setEmployee(employee);
        component.setSalaryComponent(salaryComponent);
        component.setAmount(BigDecimal.valueOf(5000000));
        component.setEffectiveDate(LocalDate.of(2024, 1, 1));
        component.setEndDate(LocalDate.of(2024, 12, 31));
        component.setRate(BigDecimal.valueOf(5));
        component.setNotes("Monthly bonus");

        assertThat(component.getEmployee()).isEqualTo(employee);
        assertThat(component.getSalaryComponent()).isEqualTo(salaryComponent);
        assertThat(component.getAmount()).isEqualTo(BigDecimal.valueOf(5000000));
        assertThat(component.getEffectiveDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(component.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 31));
    }

    @Test
    void employeeSalaryComponent_shouldCheckCurrentlyActive() {
        EmployeeSalaryComponent component = new EmployeeSalaryComponent();
        component.setEffectiveDate(LocalDate.of(2024, 1, 1));
        component.setEndDate(LocalDate.of(2024, 12, 31));

        assertThat(component.isCurrentlyActive(LocalDate.of(2024, 6, 15))).isTrue();
        assertThat(component.isCurrentlyActive(LocalDate.of(2023, 12, 31))).isFalse();
        assertThat(component.isCurrentlyActive(LocalDate.of(2025, 1, 1))).isFalse();
    }

    @Test
    void employeeSalaryComponent_shouldGetEffectiveRate() {
        EmployeeSalaryComponent component = new EmployeeSalaryComponent();
        component.setRate(BigDecimal.valueOf(10));

        assertThat(component.getEffectiveRate()).isEqualTo(BigDecimal.valueOf(10));
    }

    @Test
    void employeeSalaryComponent_shouldGetEffectiveAmount() {
        EmployeeSalaryComponent component = new EmployeeSalaryComponent();
        component.setAmount(BigDecimal.valueOf(500000));

        assertThat(component.getEffectiveAmount()).isEqualTo(BigDecimal.valueOf(500000));
    }

    @Test
    void taxTransactionDetail_shouldStoreValues() {
        TaxTransactionDetail detail = new TaxTransactionDetail();
        Transaction transaction = new Transaction();
        detail.setTransaction(transaction);
        detail.setTaxAmount(BigDecimal.valueOf(110000));
        detail.setDpp(BigDecimal.valueOf(1000000));
        detail.setTaxRate(BigDecimal.valueOf(11));
        detail.setPpn(BigDecimal.valueOf(110000));
        detail.setGrossAmount(BigDecimal.valueOf(1100000));

        assertThat(detail.getTransaction()).isEqualTo(transaction);
        assertThat(detail.getTaxAmount()).isEqualTo(BigDecimal.valueOf(110000));
        assertThat(detail.getDpp()).isEqualTo(BigDecimal.valueOf(1000000));
        assertThat(detail.getTaxRate()).isEqualTo(BigDecimal.valueOf(11));
    }

    @Test
    void taxTransactionDetail_shouldCheckIsEFaktur() {
        TaxTransactionDetail detail = new TaxTransactionDetail();
        detail.setTaxType(TaxType.PPN_KELUARAN);
        assertThat(detail.isEFaktur()).isTrue();

        detail.setTaxType(TaxType.PPN_MASUKAN);
        assertThat(detail.isEFaktur()).isTrue();

        detail.setTaxType(TaxType.PPH_21);
        assertThat(detail.isEFaktur()).isFalse();
    }

    @Test
    void taxTransactionDetail_shouldCheckIsEBupot() {
        TaxTransactionDetail detail = new TaxTransactionDetail();
        detail.setTaxType(TaxType.PPH_21);
        assertThat(detail.isEBupot()).isTrue();

        detail.setTaxType(TaxType.PPH_23);
        assertThat(detail.isEBupot()).isTrue();

        detail.setTaxType(TaxType.PPN_KELUARAN);
        assertThat(detail.isEBupot()).isFalse();
    }

    @Test
    void taxTransactionDetail_shouldGetCounterpartyIdNumber() {
        TaxTransactionDetail detail = new TaxTransactionDetail();
        detail.setCounterpartyIdType("NIK");
        detail.setCounterpartyNik("1234567890123456");
        detail.setCounterpartyNpwp("123456789012345");

        assertThat(detail.getCounterpartyIdNumber()).isEqualTo("1234567890123456");

        detail.setCounterpartyIdType("TIN");
        assertThat(detail.getCounterpartyIdNumber()).isEqualTo("123456789012345");
    }

    @Test
    void auditLog_shouldStoreValues() {
        AuditLog log = new AuditLog();
        UUID entityId = UUID.randomUUID();
        log.setAction("CREATE");
        log.setEntityType("Transaction");
        log.setEntityId(entityId);
        log.setIpAddress("192.168.1.1");
        log.setUserAgent("Mozilla/5.0");
        log.setOldValues(Map.of("field", "old"));
        log.setNewValues(Map.of("field", "new"));

        assertThat(log.getAction()).isEqualTo("CREATE");
        assertThat(log.getEntityType()).isEqualTo("Transaction");
        assertThat(log.getEntityId()).isEqualTo(entityId);
        assertThat(log.getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(log.getUserAgent()).isEqualTo("Mozilla/5.0");
    }
}

package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.dto.dataimport.TemplateImportFileDto;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify complex formulas with dynamic variables
 * are properly stored in the database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("Complex Template Import - Integration Tests")
@Sql(scripts = {"/db/testmigration/cleanup-for-clear-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ComplexTemplateImportTest {

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private JournalTemplateRepository templateRepository;

    @Test
    @DisplayName("Should import and store complex formulas with dynamic variables in database")
    @Transactional
    void shouldImportAndStoreComplexFormulasInDatabase() throws Exception {
        // Load the complex template JSON file
        ObjectMapper objectMapper = new ObjectMapper();
        File templateFile = new File("src/test/resources/import-test-data/template-complex.json");
        TemplateImportFileDto importDto = objectMapper.readValue(templateFile, TemplateImportFileDto.class);

        // Import templates (clearing existing ones)
        var result = dataImportService.importTemplate(importDto, true);
        
        // Print errors if import failed
        if (!result.success()) {
            System.out.println("Import failed with errors:");
            result.errors().forEach(error -> {
                System.out.println("  - " + error.field() + ": " + error.message());
            });
        }
        
        // Verify import was successful
        assertThat(result.success())
            .withFailMessage("Import failed: %s", result.message())
            .isTrue();
        assertThat(result.successCount()).isEqualTo(4); // 4 templates in the file
        assertThat(result.errorCount()).isZero();

        // Verify Payroll Template with dynamic variables is stored correctly
        var payrollTemplate = templateRepository.findByTemplateName("Test Payroll Template");
        assertThat(payrollTemplate).isPresent();
        
        var payrollWithLines = templateRepository.findByIdWithLines(payrollTemplate.get().getId());
        assertThat(payrollWithLines).isPresent();
        
        var lines = payrollWithLines.get().getLines();
        assertThat(lines).hasSize(6);
        
        // Verify formulas are stored exactly as specified in JSON
        assertThat(lines.get(0).getFormula()).isEqualTo("grossSalary");
        assertThat(lines.get(0).getDescription()).isEqualTo("Beban gaji karyawan (gross)");
        
        assertThat(lines.get(1).getFormula()).isEqualTo("companyBpjs * 0.8");
        assertThat(lines.get(1).getDescription()).isEqualTo("Beban BPJS Kesehatan perusahaan");
        
        assertThat(lines.get(2).getFormula()).isEqualTo("companyBpjs * 0.2");
        assertThat(lines.get(2).getDescription()).isEqualTo("Beban BPJS Ketenagakerjaan perusahaan");
        
        assertThat(lines.get(3).getFormula()).isEqualTo("netPay");
        assertThat(lines.get(3).getDescription()).isEqualTo("Hutang gaji karyawan (net pay)");
        
        assertThat(lines.get(4).getFormula()).isEqualTo("totalBpjs");
        assertThat(lines.get(4).getDescription()).isEqualTo("Hutang BPJS (perusahaan + karyawan)");
        
        assertThat(lines.get(5).getFormula()).isEqualTo("pph21");
        assertThat(lines.get(5).getDescription()).isEqualTo("Hutang PPh 21");
        
        // Verify Fee Template formulas
        var feeTemplate = templateRepository.findByTemplateName("Test Fee Template");
        assertThat(feeTemplate).isPresent();
        
        var feeWithLines = templateRepository.findByIdWithLines(feeTemplate.get().getId());
        var feeLines = feeWithLines.get().getLines();
        assertThat(feeLines).hasSize(3);
        
        assertThat(feeLines.get(0).getFormula()).isEqualTo("fee");
        assertThat(feeLines.get(1).getFormula()).isEqualTo("fee * 0.11");
        assertThat(feeLines.get(2).getFormula()).isEqualTo("fee * 0.89");
        
        // Verify Conditional Formula Template
        var conditionalTemplate = templateRepository.findByTemplateName("Test Conditional Formula");
        assertThat(conditionalTemplate).isPresent();
        
        var conditionalWithLines = templateRepository.findByIdWithLines(conditionalTemplate.get().getId());
        var conditionalLines = conditionalWithLines.get().getLines();
        assertThat(conditionalLines).hasSize(3);
        
        assertThat(conditionalLines.get(0).getFormula()).isEqualTo("amount");
        assertThat(conditionalLines.get(1).getFormula()).isEqualTo("amount - (amount > 2000000 ? amount * 0.02 : 0)");
        assertThat(conditionalLines.get(2).getFormula()).isEqualTo("amount > 2000000 ? amount * 0.02 : 0");
        
        // Verify Multiple Variables Template
        var multiVarTemplate = templateRepository.findByTemplateName("Test Multiple Variables");
        assertThat(multiVarTemplate).isPresent();
        
        var multiVarWithLines = templateRepository.findByIdWithLines(multiVarTemplate.get().getId());
        var multiVarLines = multiVarWithLines.get().getLines();
        assertThat(multiVarLines).hasSize(3);
        
        assertThat(multiVarLines.get(0).getFormula()).isEqualTo("principal + interest");
        assertThat(multiVarLines.get(1).getFormula()).isEqualTo("adminFee");
        assertThat(multiVarLines.get(2).getFormula()).isEqualTo("principal + interest + adminFee");
    }
}

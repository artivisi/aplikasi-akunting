package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.DataImportPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Data Import - Functional Tests")
class DataImportTest extends PlaywrightTestBase {

    @Autowired
    private JournalTemplateRepository templateRepository;

    private LoginPage loginPage;
    private DataImportPage importPage;

    private static final Path COA_TEST_FILE = Paths.get("src/test/resources/import-test-data/coa-test.json");
    private static final Path TEMPLATE_TEST_FILE = Paths.get("src/test/resources/import-test-data/template-test.json");
    private static final Path TEMPLATE_COMPLEX_FILE = Paths.get("src/test/resources/import-test-data/template-complex.json");

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        importPage = new DataImportPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @DisplayName("Should display import page with navigation link")
    void shouldDisplayImportPageWithNavigation() {
        importPage.navigate();

        importPage.assertPageTitleVisible();
        importPage.assertPageTitleText("Import Data");
        importPage.assertNavigationImportLinkVisible();
    }

    @Test
    @DisplayName("Should display COA and Template import links")
    void shouldDisplayImportLinks() {
        importPage.navigate();

        importPage.assertCOAImportLinkVisible();
        importPage.assertTemplateImportLinkVisible();
    }

    @Test
    @DisplayName("Should display download links for sample files")
    void shouldDisplayDownloadLinks() {
        importPage.navigate();

        importPage.assertCOASampleDownloadVisible();
        importPage.assertTemplateSampleDownloadVisible();
    }

    @Test
    @DisplayName("Should navigate to COA import page")
    void shouldNavigateToCOAImportPage() {
        importPage.navigate();
        importPage.clickCOAImportLink();

        importPage.assertPageTitleText("Import Bagan Akun");
    }

    @Test
    @DisplayName("Should navigate to Template import page")
    void shouldNavigateToTemplateImportPage() {
        importPage.navigate();
        importPage.clickTemplateImportLink();

        importPage.assertPageTitleText("Import Template Jurnal");
    }

    @Test
    @DisplayName("Should display COA import form elements")
    void shouldDisplayCOAImportFormElements() {
        importPage.navigateToCOAImport();

        importPage.assertFileInputVisible();
        importPage.assertPreviewButtonVisible();
    }

    @Test
    @DisplayName("Should display Template import form elements")
    void shouldDisplayTemplateImportFormElements() {
        importPage.navigateToTemplateImport();

        importPage.assertFileInputVisible();
        importPage.assertPreviewButtonVisible();
    }

    @Test
    @DisplayName("Should preview COA import file")
    void shouldPreviewCOAImportFile() {
        importPage.navigateToCOAImport();

        importPage.uploadFile(COA_TEST_FILE.toAbsolutePath());
        importPage.clickPreview();

        importPage.assertPreviewHasContent();
    }

    @Test
    @DisplayName("Should preview Template import file")
    void shouldPreviewTemplateImportFile() {
        importPage.navigateToTemplateImport();

        importPage.uploadFile(TEMPLATE_TEST_FILE.toAbsolutePath());
        importPage.clickPreview();

        importPage.assertPreviewHasContent();
    }

    @Test
    @DisplayName("Should clear existing COA and import new data")
    @Sql(scripts = "/db/testmigration/cleanup-for-clear-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldClearExistingCOAAndImportNewData() {
        importPage.navigateToCOAImport();

        // Upload and preview
        importPage.uploadFile(COA_TEST_FILE.toAbsolutePath());
        importPage.clickPreview();
        importPage.assertPreviewHasContent();

        // Import with clear existing option
        importPage.uploadFileInPreview(COA_TEST_FILE.toAbsolutePath());
        importPage.checkClearExistingInPreview();
        importPage.clickImportNow();

        // Should redirect to result page with success
        importPage.assertResultPageVisible();
        importPage.assertImportSuccess();

        // Navigate to COA list and verify imported accounts exist
        page.navigate(baseUrl() + "/accounts");
        page.waitForLoadState();

        // The test accounts (9, 9.1) should exist
        assertThat(page.locator("text=TEST IMPORT")).isVisible();
    }

    @Test
    @DisplayName("Should clear existing Templates and import new data")
    @Sql(scripts = "/db/testmigration/cleanup-for-clear-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldClearExistingTemplatesAndImportNewData() {
        importPage.navigateToTemplateImport();

        // Upload and preview
        importPage.uploadFile(TEMPLATE_TEST_FILE.toAbsolutePath());
        importPage.clickPreview();
        importPage.assertPreviewHasContent();

        // Import with clear existing option
        importPage.uploadFileInPreview(TEMPLATE_TEST_FILE.toAbsolutePath());
        importPage.checkClearExistingInPreview();
        importPage.clickImportNow();

        // Should redirect to result page with success
        importPage.assertResultPageVisible();
        importPage.assertImportSuccess();

        // Navigate to Templates list and verify imported template exists
        page.navigate(baseUrl() + "/templates");
        page.waitForLoadState();

        // The test template should exist
        assertThat(page.locator("text=Test Import Template")).isVisible();
    }

    @Test
    @DisplayName("Should import complex templates with dynamic variables and store in database")
    @Sql(scripts = "/db/testmigration/cleanup-for-clear-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldImportComplexTemplatesWithDynamicVariables() {
        importPage.navigateToTemplateImport();

        // Upload and preview complex template file
        importPage.uploadFile(TEMPLATE_COMPLEX_FILE.toAbsolutePath());
        importPage.clickPreview();
        importPage.assertPreviewHasContent();

        // Import with clear existing option
        importPage.uploadFileInPreview(TEMPLATE_COMPLEX_FILE.toAbsolutePath());
        importPage.checkClearExistingInPreview();
        importPage.clickImportNow();

        // Should redirect to result page with success
        importPage.assertResultPageVisible();
        importPage.assertImportSuccess();

        // Verify templates are stored in database with correct formulas
        var payrollTemplate = templateRepository.findByTemplateName("Test Payroll Template");
        assertThat(payrollTemplate).isPresent();
        
        // Fetch with lines to verify formulas
        var payrollWithLines = templateRepository.findByIdWithLines(payrollTemplate.get().getId());
        assertThat(payrollWithLines).isPresent();
        
        var lines = payrollWithLines.get().getLines();
        assertThat(lines).hasSize(6);
        
        // Verify each formula is stored correctly in database
        // Note: lines are sorted by line_order, so we can check by index
        assertThat(lines.get(0).getFormula()).isEqualTo("grossSalary");
        assertThat(lines.get(1).getFormula()).isEqualTo("companyBpjs * 0.8");
        assertThat(lines.get(2).getFormula()).isEqualTo("companyBpjs * 0.2");
        assertThat(lines.get(3).getFormula()).isEqualTo("netPay");
        assertThat(lines.get(4).getFormula()).isEqualTo("totalBpjs");
        assertThat(lines.get(5).getFormula()).isEqualTo("pph21");
        
        // Verify fee template formulas
        var feeTemplate = templateRepository.findByTemplateName("Test Fee Template");
        assertThat(feeTemplate).isPresent();
        
        var feeWithLines = templateRepository.findByIdWithLines(feeTemplate.get().getId());
        assertThat(feeWithLines).isPresent();
        
        var feeLines = feeWithLines.get().getLines();
        assertThat(feeLines).hasSize(3);
        assertThat(feeLines.get(0).getFormula()).isEqualTo("fee");
        assertThat(feeLines.get(1).getFormula()).isEqualTo("fee * 0.11");
        assertThat(feeLines.get(2).getFormula()).isEqualTo("fee * 0.89");
        
        // Verify conditional formula template
        var conditionalTemplate = templateRepository.findByTemplateName("Test Conditional Formula");
        assertThat(conditionalTemplate).isPresent();
        
        var conditionalWithLines = templateRepository.findByIdWithLines(conditionalTemplate.get().getId());
        var conditionalLines = conditionalWithLines.get().getLines();
        assertThat(conditionalLines).hasSize(3);
        assertThat(conditionalLines.get(0).getFormula()).isEqualTo("amount");
        assertThat(conditionalLines.get(1).getFormula()).isEqualTo("amount - (amount > 2000000 ? amount * 0.02 : 0)");
        assertThat(conditionalLines.get(2).getFormula()).isEqualTo("amount > 2000000 ? amount * 0.02 : 0");
        
        // Verify multiple variables template
        var multiVarTemplate = templateRepository.findByTemplateName("Test Multiple Variables");
        assertThat(multiVarTemplate).isPresent();
        
        var multiVarWithLines = templateRepository.findByIdWithLines(multiVarTemplate.get().getId());
        var multiVarLines = multiVarWithLines.get().getLines();
        assertThat(multiVarLines).hasSize(3);
        assertThat(multiVarLines.get(0).getFormula()).isEqualTo("principal + interest");
        assertThat(multiVarLines.get(1).getFormula()).isEqualTo("adminFee");
        assertThat(multiVarLines.get(2).getFormula()).isEqualTo("principal + interest + adminFee");
    }

    @Test
    @DisplayName("Should validate formulas with dynamic variables during preview")
    void shouldValidateFormulasWithDynamicVariablesDuringPreview() {
        importPage.navigateToTemplateImport();

        // Upload complex template file with dynamic variables
        importPage.uploadFile(TEMPLATE_COMPLEX_FILE.toAbsolutePath());
        importPage.clickPreview();

        // Preview should display content without validation errors
        // Simple identifiers like 'grossSalary', 'fee', 'companyBpjs' should be accepted
        importPage.assertPreviewHasContent();
        
        // Should not show error messages for valid dynamic variables
        assertThat(page.locator("text=Property or field 'grossSalary' cannot be found")).not().isVisible();
        assertThat(page.locator("text=Property or field 'fee' cannot be found")).not().isVisible();
        assertThat(page.locator("text=Property or field 'companyBpjs' cannot be found")).not().isVisible();
    }
}

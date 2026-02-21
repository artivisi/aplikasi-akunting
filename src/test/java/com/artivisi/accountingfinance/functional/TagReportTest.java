package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.Tag;
import com.artivisi.accountingfinance.entity.TagType;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.TagRepository;
import com.artivisi.accountingfinance.repository.TagTypeRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Tag Report Tests")
@Import(ServiceTestDataInitializer.class)
class TagReportTest extends PlaywrightTestBase {

    @Autowired
    private TagTypeRepository tagTypeRepository;

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
        ensureTestTags();
    }

    private void ensureTestTags() {
        if (tagTypeRepository.findAllActive().stream().noneMatch(t -> "RPT".equals(t.getCode()))) {
            TagType type = new TagType();
            type.setCode("RPT");
            type.setName("Report Test");
            type.setActive(true);
            type = tagTypeRepository.save(type);

            Tag tag = new Tag();
            tag.setTagType(type);
            tag.setCode("RT1");
            tag.setName("Report Tag 1");
            tag.setActive(true);
            tagRepository.save(tag);
        }
    }

    @Test
    @DisplayName("Should display tag summary report page")
    void shouldDisplayTagSummaryPage() {
        navigateTo("/reports/tag-summary");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).hasText("Ringkasan per Label");
        assertThat(page.locator("#startDate")).isVisible();
        assertThat(page.locator("#endDate")).isVisible();
        assertThat(page.locator("#btn-filter")).isVisible();
    }

    @Test
    @DisplayName("Should filter tag report by date range")
    void shouldFilterByDateRange() {
        navigateTo("/reports/tag-summary?startDate=2025-01-01&endDate=2025-12-31");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).hasText("Ringkasan per Label");
        // Date inputs should be populated with the query params
        assertThat(page.locator("#startDate")).hasValue("2025-01-01");
        assertThat(page.locator("#endDate")).hasValue("2025-12-31");
    }

    @Test
    @DisplayName("Should show tag summary link on reports index page")
    void shouldShowTagSummaryLinkOnReportsIndex() {
        navigateTo("/reports");
        waitForPageLoad();

        var link = page.locator("a[href='/reports/tag-summary']");
        assertThat(link).isVisible();
        assertThat(page.locator("text=Ringkasan per Label")).isVisible();
    }

    @Test
    @DisplayName("Should show empty state when no tagged transactions")
    void shouldShowEmptyStateWhenNoTaggedTransactions() {
        // Query a date range with no tagged transactions
        navigateTo("/reports/tag-summary?startDate=2000-01-01&endDate=2000-12-31");
        waitForPageLoad();

        assertThat(page.locator("text=Tidak ada data transaksi dengan label pada periode ini")).isVisible();
    }
}

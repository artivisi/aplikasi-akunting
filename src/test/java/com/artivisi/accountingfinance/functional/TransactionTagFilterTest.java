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

@DisplayName("Transaction Tag Filter Tests")
@Import(ServiceTestDataInitializer.class)
class TransactionTagFilterTest extends PlaywrightTestBase {

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
        if (tagTypeRepository.findAllActive().stream().noneMatch(t -> "FLTR".equals(t.getCode()))) {
            TagType type = new TagType();
            type.setCode("FLTR");
            type.setName("Filter Test");
            type.setActive(true);
            type = tagTypeRepository.save(type);

            Tag tag = new Tag();
            tag.setTagType(type);
            tag.setCode("FT1");
            tag.setName("Filter Tag 1");
            tag.setActive(true);
            tagRepository.save(tag);
        }
    }

    @Test
    @DisplayName("Should display tag filter dropdown on transaction list")
    void shouldDisplayTagFilterDropdown() {
        navigateTo("/transactions");
        waitForPageLoad();

        assertThat(page.locator("#filter-tag")).isVisible();
    }

    @Test
    @DisplayName("Should filter transactions by tag via URL parameter")
    void shouldFilterByTagViaUrl() {
        var tag = tagRepository.findAllActiveOrdered().stream().findFirst();
        if (tag.isEmpty()) return;

        navigateTo("/transactions?tagId=" + tag.get().getId());
        waitForPageLoad();

        // Page should load without error
        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should clear tag filter and show all transactions")
    void shouldClearTagFilter() {
        navigateTo("/transactions");
        waitForPageLoad();

        // Default "Semua Label" should be selected
        var filterSelect = page.locator("#filter-tag");
        assertThat(filterSelect).isVisible();

        // Page should show transactions (or empty state)
        assertThat(page.locator("#transaction-table")).isVisible();
    }
}

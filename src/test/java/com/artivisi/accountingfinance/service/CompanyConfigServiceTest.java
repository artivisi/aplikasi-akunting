package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.CompanyConfig;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for CompanyConfigService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("CompanyConfigService Integration Tests")
class CompanyConfigServiceTest {

    @Autowired
    private CompanyConfigService companyConfigService;

    @Nested
    @DisplayName("Get Config Operations")
    class GetConfigTests {

        @Test
        @DisplayName("Should get or create config")
        void shouldGetOrCreateConfig() {
            CompanyConfig config = companyConfigService.getConfig();

            assertThat(config).isNotNull();
            assertThat(config.getId()).isNotNull();
        }

        @Test
        @DisplayName("Should return existing config")
        void shouldReturnExistingConfig() {
            CompanyConfig first = companyConfigService.getConfig();
            CompanyConfig second = companyConfigService.getConfig();

            assertThat(first.getId()).isEqualTo(second.getId());
        }

        @Test
        @DisplayName("Should throw exception for non-existent ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> companyConfigService.findById(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should update company config")
        void shouldUpdateCompanyConfig() {
            CompanyConfig existing = companyConfigService.getConfig();

            CompanyConfig updated = new CompanyConfig();
            updated.setCompanyName("Updated Company Name");
            updated.setCompanyAddress("123 Test Street");
            updated.setCompanyPhone("021-1234567");
            updated.setCompanyEmail("test@company.com");
            updated.setTaxId("123456789");
            updated.setNpwp("12.345.678.9-012.000");
            updated.setNitku("1234567890123456");
            updated.setFiscalYearStartMonth(1);
            updated.setCurrencyCode("IDR");
            updated.setSigningOfficerName("John Doe");
            updated.setSigningOfficerTitle("CEO");

            CompanyConfig result = companyConfigService.update(existing.getId(), updated);

            assertThat(result).isNotNull();
            assertThat(result.getCompanyName()).isEqualTo("Updated Company Name");
            assertThat(result.getCompanyAddress()).isEqualTo("123 Test Street");
            assertThat(result.getCompanyEmail()).isEqualTo("test@company.com");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should update with non-existent ID but get/create config")
        void shouldUpdateWithNonExistentIdButGetOrCreateConfig() {
            UUID randomId = UUID.randomUUID();

            CompanyConfig updated = new CompanyConfig();
            updated.setCompanyName("New Company");
            updated.setFiscalYearStartMonth(1);
            updated.setCurrencyCode("IDR");

            CompanyConfig result = companyConfigService.update(randomId, updated);

            assertThat(result).isNotNull();
            assertThat(result.getCompanyName()).isEqualTo("New Company");
        }
    }

    @Nested
    @DisplayName("Save Operations")
    class SaveTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should save company config")
        void shouldSaveCompanyConfig() {
            CompanyConfig config = new CompanyConfig();
            config.setCompanyName("Test Company");
            config.setFiscalYearStartMonth(4);
            config.setCurrencyCode("USD");

            CompanyConfig saved = companyConfigService.save(config);

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getCompanyName()).isEqualTo("Test Company");
            assertThat(saved.getFiscalYearStartMonth()).isEqualTo(4);
            assertThat(saved.getCurrencyCode()).isEqualTo("USD");
        }
    }

    @Nested
    @DisplayName("Default Config")
    class DefaultConfigTests {

        @Test
        @DisplayName("Should have default fiscal year start month")
        void shouldHaveDefaultFiscalYearStartMonth() {
            CompanyConfig config = companyConfigService.getConfig();

            assertThat(config.getFiscalYearStartMonth()).isNotNull();
            assertThat(config.getFiscalYearStartMonth()).isBetween(1, 12);
        }

        @Test
        @DisplayName("Should have currency code")
        void shouldHaveCurrencyCode() {
            CompanyConfig config = companyConfigService.getConfig();

            assertThat(config.getCurrencyCode()).isNotNull();
        }
    }
}

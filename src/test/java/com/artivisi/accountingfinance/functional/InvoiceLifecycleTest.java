package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.Bill;
import com.artivisi.accountingfinance.entity.BillLine;
import com.artivisi.accountingfinance.entity.Client;
import com.artivisi.accountingfinance.entity.Invoice;
import com.artivisi.accountingfinance.entity.InvoiceLine;
import com.artivisi.accountingfinance.entity.Vendor;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ClientRepository;
import com.artivisi.accountingfinance.repository.VendorRepository;
import com.artivisi.accountingfinance.service.BillService;
import com.artivisi.accountingfinance.service.InvoiceService;
import com.artivisi.accountingfinance.service.VendorService;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Slf4j
@DisplayName("Invoice & Bill Lifecycle - E2E Test with Screenshots")
@Import(ServiceTestDataInitializer.class)
class InvoiceLifecycleTest extends PlaywrightTestBase {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private BillService billService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private VendorRepository vendorRepository;

    private String invoiceNumber;
    private String billNumber;
    private Client testClient;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null, Collections.emptyList()));

        try {
            testClient = clientRepository.findByCode("TELKOM").orElseThrow();

            // Create invoice with line items
            Invoice invoice = new Invoice();
            invoice.setClient(testClient);
            invoice.setInvoiceDate(LocalDate.of(2026, 2, 1));
            invoice.setDueDate(LocalDate.of(2026, 3, 1));
            invoice.setAmount(new BigDecimal("15000000"));

            InvoiceLine line1 = new InvoiceLine();
            line1.setDescription("Jasa Pengembangan Aplikasi");
            line1.setQuantity(BigDecimal.ONE);
            line1.setUnitPrice(new BigDecimal("10000000"));
            line1.calculateAmounts();

            InvoiceLine line2 = new InvoiceLine();
            line2.setDescription("Jasa Maintenance Bulanan");
            line2.setQuantity(new BigDecimal("5"));
            line2.setUnitPrice(new BigDecimal("1000000"));
            line2.calculateAmounts();

            Invoice saved = invoiceService.create(invoice, List.of(line1, line2));
            invoiceNumber = saved.getInvoiceNumber();

            // Create vendor and bill
            String vendorCode = "VND-LIFECYCLE";
            var existing = vendorRepository.findByCode(vendorCode);
            Vendor vendor;
            if (existing.isPresent()) {
                vendor = existing.get();
            } else {
                vendor = new Vendor();
                vendor.setCode(vendorCode);
                vendor.setName("PT Cloud Hosting Indonesia");
                vendor.setActive(true);
                vendor = vendorService.create(vendor);
            }

            Bill bill = new Bill();
            bill.setVendor(vendor);
            bill.setBillDate(LocalDate.of(2026, 2, 5));
            bill.setDueDate(LocalDate.of(2026, 3, 5));
            bill.setAmount(new BigDecimal("6000000"));

            BillLine billLine = new BillLine();
            billLine.setDescription("Sewa Server Cloud 6 Bulan");
            billLine.setQuantity(new BigDecimal("6"));
            billLine.setUnitPrice(new BigDecimal("1000000"));
            billLine.calculateAmounts();

            Bill savedBill = billService.create(bill, List.of(billLine));
            billService.approve(savedBill.getId());
            billNumber = savedBill.getBillNumber();
        } finally {
            SecurityContextHolder.clearContext();
        }

        loginAsAdmin();
    }

    @Test
    @DisplayName("Invoice lifecycle: create → send → partial payment → full payment → statement")
    void invoiceFullLifecycle() {
        // Step 1: View created invoice
        navigateTo("/invoices/" + invoiceNumber);
        waitForPageLoad();
        assertThat(page.locator("[data-testid='invoice-detail']")).isVisible();
        takeManualScreenshot("10-invoice-created");
        log.info("Step 1: Invoice created - {}", invoiceNumber);

        // Step 2: Send invoice
        page.onDialog(dialog -> dialog.accept());
        page.locator("#form-send button[type='submit']").click();
        waitForPageLoad();
        assertThat(page.locator("text=Terkirim")).isVisible();
        takeManualScreenshot("10-invoice-sent");
        log.info("Step 2: Invoice sent");

        // Step 3: View receivables aging (unpaid)
        navigateTo("/reports/aging/receivables");
        waitForPageLoad();
        assertThat(page.locator("[data-testid='aging-receivables']")).isVisible();
        takeManualScreenshot("10-aging-receivables-unpaid");
        log.info("Step 3: Aging report - unpaid");

        // Step 4: Record partial payment
        navigateTo("/invoices/" + invoiceNumber);
        waitForPageLoad();
        page.locator("[data-testid='toggle-payment-form']").click();
        page.fill("#paymentDate", "2026-02-15");
        page.fill("#amount", "5000000");
        page.selectOption("#paymentMethod", "TRANSFER");
        page.fill("#referenceNumber", "TRF-PARTIAL-001");
        page.locator("[data-testid='submit-payment']").click();
        waitForPageLoad();
        assertThat(page.locator("text=Sebagian")).isVisible();
        takeManualScreenshot("10-invoice-partial-payment");
        log.info("Step 4: Partial payment recorded");

        // Step 5: View receivables aging (reduced)
        navigateTo("/reports/aging/receivables");
        waitForPageLoad();
        takeManualScreenshot("10-aging-receivables-partial");
        log.info("Step 5: Aging report - after partial payment");

        // Step 6: Record final payment
        navigateTo("/invoices/" + invoiceNumber);
        waitForPageLoad();
        page.locator("[data-testid='toggle-payment-form']").click();
        page.fill("#paymentDate", "2026-02-20");
        page.fill("#amount", "10000000");
        page.selectOption("#paymentMethod", "TRANSFER");
        page.fill("#referenceNumber", "TRF-FINAL-001");
        page.locator("[data-testid='submit-payment']").click();
        waitForPageLoad();
        assertThat(page.locator("text=Lunas")).isVisible();
        takeManualScreenshot("10-invoice-paid");
        log.info("Step 6: Final payment - invoice PAID");

        // Step 7: View receivables aging (cleared)
        navigateTo("/reports/aging/receivables");
        waitForPageLoad();
        takeManualScreenshot("10-aging-receivables-cleared");
        log.info("Step 7: Aging report - after full payment");

        // Step 8: View client statement
        navigateTo("/statements/client/" + testClient.getCode() + "?dateFrom=2026-02-01&dateTo=2026-02-28");
        waitForPageLoad();
        assertThat(page.locator("[data-testid='client-statement']")).isVisible();
        takeManualScreenshot("10-client-statement");
        log.info("Step 8: Client statement");
    }

    @Test
    @DisplayName("Bill lifecycle: approve → payment → vendor statement")
    void billFullLifecycle() {
        // Step 1: View approved bill
        navigateTo("/bills/" + billNumber);
        waitForPageLoad();
        assertThat(page.locator("[data-testid='bill-detail']")).isVisible();
        assertThat(page.locator("span.rounded-full:has-text('Disetujui')")).isVisible();
        takeManualScreenshot("10-bill-approved");
        log.info("Step 1: Bill approved - {}", billNumber);

        // Step 2: View payables aging (unpaid)
        navigateTo("/reports/aging/payables");
        waitForPageLoad();
        assertThat(page.locator("[data-testid='aging-payables']")).isVisible();
        takeManualScreenshot("10-aging-payables-unpaid");
        log.info("Step 2: Payables aging - unpaid");

        // Step 3: Record payment
        navigateTo("/bills/" + billNumber);
        waitForPageLoad();
        page.locator("[data-testid='toggle-payment-form']").click();
        page.fill("#paymentDate", "2026-02-25");
        page.fill("#amount", "6000000");
        page.selectOption("#paymentMethod", "TRANSFER");
        page.fill("#referenceNumber", "TRF-BILL-LC-001");
        page.locator("[data-testid='submit-payment']").click();
        waitForPageLoad();
        assertThat(page.locator("text=Lunas")).isVisible();
        takeManualScreenshot("10-bill-payment");
        log.info("Step 3: Bill payment recorded - PAID");

        // Step 4: View vendor statement
        navigateTo("/statements/vendor/VND-LIFECYCLE?dateFrom=2026-02-01&dateTo=2026-02-28");
        waitForPageLoad();
        assertThat(page.locator("[data-testid='vendor-statement']")).isVisible();
        takeManualScreenshot("10-vendor-statement");
        log.info("Step 4: Vendor statement");
    }
}

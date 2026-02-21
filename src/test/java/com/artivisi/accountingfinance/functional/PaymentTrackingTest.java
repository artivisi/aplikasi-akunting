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
import com.microsoft.playwright.Locator;
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
import java.util.concurrent.atomic.AtomicInteger;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("Payment Tracking - Functional Tests")
@Import(ServiceTestDataInitializer.class)
class PaymentTrackingTest extends PlaywrightTestBase {

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

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private String sentInvoiceNumber;
    private String approvedBillNumber;

    @BeforeEach
    void setUp() {
        int seq = COUNTER.incrementAndGet();

        // Set up SecurityContext for service calls that need authentication (e.g. approve)
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null, Collections.emptyList()));

        try {
            // Create a test invoice and send it
            Client client = clientRepository.findByCode("MANDIRI").orElseThrow();

            Invoice invoice = new Invoice();
            invoice.setClient(client);
            invoice.setInvoiceDate(LocalDate.of(2026, 2, 1));
            invoice.setDueDate(LocalDate.of(2026, 3, 1));
            invoice.setAmount(new BigDecimal("10000000"));

            InvoiceLine line = new InvoiceLine();
            line.setDescription("Jasa Konsultasi IT");
            line.setQuantity(BigDecimal.ONE);
            line.setUnitPrice(new BigDecimal("10000000"));
            line.calculateAmounts();

            Invoice saved = invoiceService.create(invoice, List.of(line));
            invoiceService.send(saved.getId());
            sentInvoiceNumber = saved.getInvoiceNumber();

            // Create a test vendor and bill, then approve it
            String vendorCode = "VND-PMT-" + seq;
            Vendor vendor;
            var existing = vendorRepository.findByCode(vendorCode);
            if (existing.isPresent()) {
                vendor = existing.get();
            } else {
                vendor = new Vendor();
                vendor.setCode(vendorCode);
                vendor.setName("PT Vendor Payment Test " + seq);
                vendor.setActive(true);
                vendor = vendorService.create(vendor);
            }

            Bill bill = new Bill();
            bill.setVendor(vendor);
            bill.setBillDate(LocalDate.of(2026, 2, 5));
            bill.setDueDate(LocalDate.of(2026, 3, 5));
            bill.setAmount(new BigDecimal("5000000"));

            BillLine billLine = new BillLine();
            billLine.setDescription("Pembelian Server");
            billLine.setQuantity(BigDecimal.ONE);
            billLine.setUnitPrice(new BigDecimal("5000000"));
            billLine.calculateAmounts();

            Bill savedBill = billService.create(bill, List.of(billLine));
            billService.approve(savedBill.getId());
            approvedBillNumber = savedBill.getBillNumber();
        } finally {
            SecurityContextHolder.clearContext();
        }

        loginAsAdmin();
    }

    @Test
    @DisplayName("Invoice detail shows payment form for sent invoice")
    void shouldShowPaymentFormForSentInvoice() {
        navigateTo("/invoices/" + sentInvoiceNumber);
        waitForPageLoad();

        assertThat(page.locator("[data-testid='invoice-detail']")).isVisible();
        assertThat(page.locator("[data-testid='payment-form-section']")).isVisible();
        assertThat(page.locator("[data-testid='toggle-payment-form']")).isVisible();

        // Click toggle to show form
        page.locator("[data-testid='toggle-payment-form']").click();

        // Payment form fields should appear
        assertThat(page.locator("#paymentDate")).isVisible();
        assertThat(page.locator("#amount")).isVisible();
        assertThat(page.locator("#paymentMethod")).isVisible();
        assertThat(page.locator("[data-testid='submit-payment']")).isVisible();

        log.info("Payment form for sent invoice verified - {}", sentInvoiceNumber);
    }

    @Test
    @DisplayName("Record partial payment on invoice shows payment history and balance due")
    void shouldRecordPartialPaymentOnInvoice() {
        navigateTo("/invoices/" + sentInvoiceNumber);
        waitForPageLoad();

        // Toggle payment form
        page.locator("[data-testid='toggle-payment-form']").click();

        // Fill payment form
        page.fill("#paymentDate", "2026-02-15");
        page.fill("#amount", "3000000");
        page.selectOption("#paymentMethod", "TRANSFER");
        page.fill("#referenceNumber", "TRF-001");

        // Submit
        page.locator("[data-testid='submit-payment']").click();
        waitForPageLoad();

        // Should redirect back to detail with success message
        assertThat(page.locator("[data-testid='invoice-detail']")).isVisible();

        // Payment history should now be visible
        assertThat(page.locator("[data-testid='payment-history']")).isVisible();

        // Balance due should show remaining amount
        assertThat(page.locator("[data-testid='balance-due']")).isVisible();
        String balanceText = page.locator("[data-testid='balance-due']").textContent();
        assertThat(balanceText).contains("7,000,000");

        // Status should be PARTIAL (Sebagian)
        assertThat(page.locator("text=Sebagian")).isVisible();

        log.info("Partial payment recorded - balance due: {}", balanceText);
    }

    @Test
    @DisplayName("Record full payment on invoice marks it as paid")
    void shouldRecordFullPaymentOnInvoice() {
        navigateTo("/invoices/" + sentInvoiceNumber);
        waitForPageLoad();

        // Toggle payment form
        page.locator("[data-testid='toggle-payment-form']").click();

        // Pay full amount
        page.fill("#paymentDate", "2026-02-20");
        page.fill("#amount", "10000000");
        page.selectOption("#paymentMethod", "TRANSFER");
        page.fill("#referenceNumber", "TRF-FULL");

        page.locator("[data-testid='submit-payment']").click();
        waitForPageLoad();

        // Status should be PAID (Lunas)
        assertThat(page.locator("text=Lunas")).isVisible();

        // Payment form should NOT be visible anymore (status is PAID)
        assertThat(page.locator("[data-testid='payment-form-section']")).not().isAttached();

        log.info("Full payment recorded - invoice marked as PAID");
    }

    @Test
    @DisplayName("Invoice detail shows total, paid, and balance amounts")
    void shouldShowPaymentAmounts() {
        navigateTo("/invoices/" + sentInvoiceNumber);
        waitForPageLoad();

        // Total amount should be visible
        String pageContent = page.content();
        assertThat(pageContent).contains("10,000,000");

        // For a sent invoice without payments, balance due should equal total
        assertThat(page.locator("[data-testid='balance-due']")).isVisible();
        String balanceText = page.locator("[data-testid='balance-due']").textContent();
        assertThat(balanceText).contains("10,000,000");

        log.info("Payment amounts displayed correctly");
    }

    @Test
    @DisplayName("Bill detail shows payment form for approved bill")
    void shouldShowPaymentFormForApprovedBill() {
        navigateTo("/bills/" + approvedBillNumber);
        waitForPageLoad();

        assertThat(page.locator("[data-testid='bill-detail']")).isVisible();
        assertThat(page.locator("[data-testid='payment-form-section']")).isVisible();
        assertThat(page.locator("[data-testid='toggle-payment-form']")).isVisible();

        // Click toggle to show form
        page.locator("[data-testid='toggle-payment-form']").click();

        assertThat(page.locator("#paymentDate")).isVisible();
        assertThat(page.locator("#amount")).isVisible();
        assertThat(page.locator("#paymentMethod")).isVisible();

        log.info("Payment form for approved bill verified - {}", approvedBillNumber);
    }

    @Test
    @DisplayName("Record payment on bill and verify status change")
    void shouldRecordPaymentOnBill() {
        navigateTo("/bills/" + approvedBillNumber);
        waitForPageLoad();

        // Toggle payment form
        page.locator("[data-testid='toggle-payment-form']").click();

        // Pay full amount
        page.fill("#paymentDate", "2026-02-25");
        page.fill("#amount", "5000000");
        page.selectOption("#paymentMethod", "TRANSFER");
        page.fill("#referenceNumber", "TRF-BILL-001");

        page.locator("[data-testid='submit-payment']").click();
        waitForPageLoad();

        // Status should be PAID (Lunas)
        assertThat(page.locator("text=Lunas")).isVisible();

        // Payment history should be visible
        assertThat(page.locator("[data-testid='payment-history']")).isVisible();

        // Payment form should NOT be visible anymore
        assertThat(page.locator("[data-testid='payment-form-section']")).not().isAttached();

        log.info("Bill payment recorded - bill marked as PAID");
    }

    @Test
    @DisplayName("Aging report shows outstanding invoices with client name")
    void shouldShowOutstandingInvoicesInAgingReport() {
        // Navigate to aging report — the sent invoice should appear
        navigateTo("/reports/aging/receivables");
        waitForPageLoad();

        assertThat(page.locator("[data-testid='aging-receivables']")).isVisible();

        // The client (MANDIRI) should appear in the aging table since it has outstanding invoices
        Locator table = page.locator("[data-testid='aging-table']");
        assertThat(table).isVisible();
        assertThat(table.locator("text=PT Bank Mandiri Tbk")).isVisible();

        log.info("Aging report shows outstanding invoices correctly");
    }
}

package com.artivisi.accountingfinance.scheduler;

import com.artivisi.accountingfinance.entity.DepreciationEntry;
import com.artivisi.accountingfinance.service.AmortizationBatchService;
import com.artivisi.accountingfinance.service.FixedAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Scheduler for automated monthly journal entries.
 * Handles both amortization entries and fixed asset depreciation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlyJournalScheduler {

    private static final String SYSTEM_USER = "SYSTEM";

    private final AmortizationBatchService amortizationBatchService;
    private final FixedAssetService fixedAssetService;

    /**
     * Run daily at 6:00 AM to process auto-post amortization entries.
     * Entries are processed if their period end date is <= today and
     * the schedule has auto_post = true.
     */
    @Scheduled(cron = "${app.amortization.schedule:0 0 6 * * *}")
    public void processAmortizationEntries() {
        log.info("Starting scheduled amortization batch processing");
        try {
            AmortizationBatchService.BatchResult result = amortizationBatchService.processAutoPostEntries(LocalDate.now());
            log.info("Scheduled amortization batch completed: {} processed, {} success, {} errors",
                    result.totalProcessed(), result.successCount(), result.errorCount());
        } catch (Exception e) {
            log.error("Scheduled amortization batch failed", e);
        }
    }

    /**
     * Run on the 1st day of each month at 7:00 AM to process depreciation entries
     * for the previous month. Entries are generated and auto-posted.
     */
    @Scheduled(cron = "${app.depreciation.schedule:0 0 7 1 * *}")
    public void processDepreciationEntries() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        log.info("Starting scheduled depreciation batch processing for period: {}", previousMonth);

        try {
            // Step 1: Generate depreciation entries
            List<DepreciationEntry> generatedEntries = fixedAssetService.generateDepreciationEntries(previousMonth);

            if (generatedEntries.isEmpty()) {
                log.info("No assets need depreciation for period {}", previousMonth);
                return;
            }

            // Step 2: Post all generated entries
            int postedCount = 0;
            int errorCount = 0;

            for (DepreciationEntry entry : generatedEntries) {
                try {
                    fixedAssetService.postDepreciationEntry(entry.getId(), SYSTEM_USER);
                    postedCount++;
                } catch (Exception e) {
                    errorCount++;
                    log.error("Failed to post depreciation entry: asset={}, period={}, error={}",
                            entry.getFixedAsset().getAssetCode(), entry.getPeriodNumber(), e.getMessage());
                }
            }

            log.info("Scheduled depreciation batch completed: {} generated, {} posted, {} errors",
                    generatedEntries.size(), postedCount, errorCount);
        } catch (Exception e) {
            log.error("Scheduled depreciation batch failed", e);
        }
    }
}

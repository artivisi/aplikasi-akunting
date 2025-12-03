package com.artivisi.accountingfinance.entity;

/**
 * Status of a depreciation entry.
 */
public enum DepreciationEntryStatus {
    /**
     * Entry generated but not yet posted.
     */
    PENDING,

    /**
     * Entry has been posted to journal.
     */
    POSTED,

    /**
     * Entry was skipped (e.g., manual decision).
     */
    SKIPPED
}

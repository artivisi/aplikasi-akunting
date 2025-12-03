package com.artivisi.accountingfinance.entity;

/**
 * Depreciation method for fixed assets.
 */
public enum DepreciationMethod {
    /**
     * Straight-line depreciation: equal amount each period.
     * Formula: (Cost - Residual Value) / Useful Life
     */
    STRAIGHT_LINE,

    /**
     * Declining balance depreciation: percentage of book value.
     * Formula: Book Value * Depreciation Rate
     */
    DECLINING_BALANCE
}

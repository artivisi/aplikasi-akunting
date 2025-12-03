package com.artivisi.accountingfinance.entity;

/**
 * Status of a fixed asset.
 */
public enum AssetStatus {
    /**
     * Asset is active and being depreciated.
     */
    ACTIVE,

    /**
     * Asset is fully depreciated but still in use.
     */
    FULLY_DEPRECIATED,

    /**
     * Asset has been disposed (sold, scrapped, or transferred).
     */
    DISPOSED
}

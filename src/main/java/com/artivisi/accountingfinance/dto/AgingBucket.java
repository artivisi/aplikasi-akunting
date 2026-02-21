package com.artivisi.accountingfinance.dto;

import java.math.BigDecimal;

public record AgingBucket(
        BigDecimal current,
        BigDecimal days1to30,
        BigDecimal days31to60,
        BigDecimal days61to90,
        BigDecimal over90,
        BigDecimal total
) {
    public static AgingBucket zero() {
        return new AgingBucket(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );
    }

    public AgingBucket add(AgingBucket other) {
        return new AgingBucket(
                this.current.add(other.current),
                this.days1to30.add(other.days1to30),
                this.days31to60.add(other.days31to60),
                this.days61to90.add(other.days61to90),
                this.over90.add(other.over90),
                this.total.add(other.total)
        );
    }
}

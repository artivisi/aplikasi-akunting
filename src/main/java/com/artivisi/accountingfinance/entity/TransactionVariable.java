package com.artivisi.accountingfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stores variable values for DETAILED template transactions.
 * Variables are user-provided amounts for each formula variable in the template.
 */
@Entity
@Table(name = "transaction_variables")
@Getter
@Setter
@NoArgsConstructor
public class TransactionVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @NotNull(message = "Transaction is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transaction", nullable = false)
    private Transaction transaction;

    @NotBlank(message = "Variable name is required")
    @Size(max = 100, message = "Variable name must not exceed 100 characters")
    @Column(name = "variable_name", nullable = false, length = 100)
    private String variableName;

    @NotNull(message = "Variable value is required")
    @Column(name = "variable_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal variableValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TransactionVariable(String variableName, BigDecimal variableValue) {
        this.variableName = variableName;
        this.variableValue = variableValue;
    }
}

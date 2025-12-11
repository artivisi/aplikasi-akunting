package com.artivisi.accountingfinance.functional.seller;

import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.entity.CostingMethod;
import com.artivisi.accountingfinance.entity.Product;
import com.artivisi.accountingfinance.entity.ProductCategory;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.ProductCategoryRepository;
import com.artivisi.accountingfinance.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Helper class for seller test data setup.
 * Creates products and categories needed for seller industry tests.
 *
 * Products match codes used in testdata/seller/transactions.csv:
 * - IP15PRO: iPhone 15 Pro (FIFO)
 * - SGS24: Samsung Galaxy S24 (FIFO)
 * - USBC: USB Cable Type-C (Weighted Average)
 * - CASE: Phone Case (Weighted Average)
 */
@Component
@RequiredArgsConstructor
public class SellerTestDataHelper {

    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ChartOfAccountRepository accountRepository;

    /**
     * Creates product categories and products matching CSV transaction codes.
     * Safe to call multiple times - checks if entities exist before creating.
     */
    public void setupProductsAndCategories() {
        // Get account mappings from seller COA
        ChartOfAccount inventoryAccount = accountRepository.findByAccountCode("1.1.20")
            .orElseThrow(() -> new RuntimeException("Inventory account 1.1.20 not found"));
        ChartOfAccount cogsAccount = accountRepository.findByAccountCode("5.1.01")
            .orElseThrow(() -> new RuntimeException("COGS account 5.1.01 not found"));
        ChartOfAccount salesAccount = accountRepository.findByAccountCode("4.1.01")
            .orElseThrow(() -> new RuntimeException("Sales account 4.1.01 not found"));

        // Create categories
        ProductCategory smartphone = categoryRepository.findByCode("PHONE")
            .orElseGet(() -> {
                ProductCategory cat = new ProductCategory();
                cat.setCode("PHONE");
                cat.setName("Smartphone");
                cat.setActive(true);
                return categoryRepository.save(cat);
            });

        ProductCategory accessories = categoryRepository.findByCode("ACC")
            .orElseGet(() -> {
                ProductCategory cat = new ProductCategory();
                cat.setCode("ACC");
                cat.setName("Accessories");
                cat.setActive(true);
                return categoryRepository.save(cat);
            });

        // Create products matching CSV transaction codes
        createProductIfNotExists("IP15PRO", "iPhone 15 Pro", "Apple iPhone 15 Pro 256GB",
            "pcs", CostingMethod.FIFO, smartphone, BigDecimal.valueOf(19000000),
            inventoryAccount, cogsAccount, salesAccount);

        createProductIfNotExists("SGS24", "Samsung Galaxy S24", "Samsung Galaxy S24 Ultra 512GB",
            "pcs", CostingMethod.FIFO, smartphone, BigDecimal.valueOf(14000000),
            inventoryAccount, cogsAccount, salesAccount);

        createProductIfNotExists("USBC", "USB Cable Type-C 1M", "USB Type-C cable 1 meter",
            "pcs", CostingMethod.WEIGHTED_AVERAGE, accessories, BigDecimal.valueOf(50000),
            inventoryAccount, cogsAccount, salesAccount);

        createProductIfNotExists("CASE", "Phone Case Silicone", "Silicone phone case universal",
            "pcs", CostingMethod.WEIGHTED_AVERAGE, accessories, BigDecimal.valueOf(35000),
            inventoryAccount, cogsAccount, salesAccount);
    }

    private void createProductIfNotExists(String code, String name, String description,
                                         String unit, CostingMethod costingMethod,
                                         ProductCategory category, BigDecimal sellingPrice,
                                         ChartOfAccount inventoryAccount,
                                         ChartOfAccount cogsAccount,
                                         ChartOfAccount salesAccount) {
        if (productRepository.findByCode(code).isEmpty()) {
            Product product = new Product();
            product.setCode(code);
            product.setName(name);
            product.setDescription(description);
            product.setUnit(unit);
            product.setCostingMethod(costingMethod);
            product.setCategory(category);
            product.setSellingPrice(sellingPrice);
            product.setInventoryAccount(inventoryAccount);
            product.setCogsAccount(cogsAccount);
            product.setSalesAccount(salesAccount);
            product.setTrackInventory(true);
            product.setActive(true);
            productRepository.save(product);
        }
    }
}

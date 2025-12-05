package com.artivisi.accountingfinance.config;

import com.artivisi.accountingfinance.security.DataMaskingUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Collections;
import java.util.Set;

/**
 * Thymeleaf configuration to add custom expression objects.
 */
@Configuration
public class ThymeleafConfig {

    @Bean
    public DataMaskingDialect dataMaskingDialect() {
        return new DataMaskingDialect();
    }

    /**
     * Custom dialect that provides data masking utilities in Thymeleaf templates.
     * Usage: ${#mask.phone(value)}, ${#mask.nik(value)}, etc.
     */
    public static class DataMaskingDialect extends AbstractDialect implements IExpressionObjectDialect {

        public DataMaskingDialect() {
            super("dataMasking");
        }

        @Override
        public IExpressionObjectFactory getExpressionObjectFactory() {
            return new DataMaskingExpressionObjectFactory();
        }
    }

    public static class DataMaskingExpressionObjectFactory implements IExpressionObjectFactory {

        private static final String MASK_EXPRESSION_OBJECT_NAME = "mask";

        @Override
        public Set<String> getAllExpressionObjectNames() {
            return Collections.singleton(MASK_EXPRESSION_OBJECT_NAME);
        }

        @Override
        public Object buildObject(org.thymeleaf.context.IExpressionContext context, String expressionObjectName) {
            if (MASK_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
                return new DataMaskingExpressionObject();
            }
            return null;
        }

        @Override
        public boolean isCacheable(String expressionObjectName) {
            return true;
        }
    }

    /**
     * Expression object available in Thymeleaf templates as #mask.
     * Example: ${#mask.phone(employee.phone)}
     */
    public static class DataMaskingExpressionObject {

        public String nik(String nik) {
            return DataMaskingUtil.maskNik(nik);
        }

        public String npwp(String npwp) {
            return DataMaskingUtil.maskNpwp(npwp);
        }

        public String bankAccount(String accountNumber) {
            return DataMaskingUtil.maskBankAccount(accountNumber);
        }

        public String phone(String phone) {
            return DataMaskingUtil.maskPhone(phone);
        }

        public String bpjs(String bpjsNumber) {
            return DataMaskingUtil.maskBpjsNumber(bpjsNumber);
        }

        public String email(String email) {
            return DataMaskingUtil.maskEmail(email);
        }

        public String middle(String value, int showFirst, int showLast) {
            return DataMaskingUtil.maskMiddle(value, showFirst, showLast);
        }
    }
}

package com.artivisi.accountingfinance.config;

import com.artivisi.accountingfinance.security.CspNonceFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.EnumSet;

/**
 * Web MVC configuration for the application.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CspNonceInterceptor cspNonceInterceptor;
    private final ThemeInterceptor themeInterceptor;

    public WebMvcConfig(CspNonceInterceptor cspNonceInterceptor, ThemeInterceptor themeInterceptor) {
        this.cspNonceInterceptor = cspNonceInterceptor;
        this.themeInterceptor = themeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add CSP nonce to all Thymeleaf templates
        registry.addInterceptor(cspNonceInterceptor);
        // Add theme config to all Thymeleaf templates
        registry.addInterceptor(themeInterceptor);
    }

    /**
     * Register CspNonceFilter to run for ALL dispatcher types including ERROR.
     * This ensures CSP headers are set even for error responses that bypass
     * the normal Spring Security filter chain (CWE-693 fix).
     */
    @Bean
    public FilterRegistrationBean<CspNonceFilter> cspNonceFilterRegistration(CspNonceFilter cspNonceFilter) {
        FilterRegistrationBean<CspNonceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(cspNonceFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        // Run for all dispatcher types: REQUEST, ERROR, FORWARD, INCLUDE, ASYNC
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }
}

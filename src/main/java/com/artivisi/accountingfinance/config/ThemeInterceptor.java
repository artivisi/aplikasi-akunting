package com.artivisi.accountingfinance.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ThemeInterceptor implements HandlerInterceptor {

    private final ThemeConfig themeConfig;
    private final DemoModeConfig demoModeConfig;

    public ThemeInterceptor(ThemeConfig themeConfig, DemoModeConfig demoModeConfig) {
        this.themeConfig = themeConfig;
        this.demoModeConfig = demoModeConfig;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                          Object handler, ModelAndView modelAndView) {
        if (modelAndView != null) {
            modelAndView.addObject("theme", themeConfig);
            modelAndView.addObject("demoMode", demoModeConfig.isDemoMode());
        }
    }
}

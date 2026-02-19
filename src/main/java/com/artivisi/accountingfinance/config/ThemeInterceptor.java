package com.artivisi.accountingfinance.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ThemeInterceptor implements HandlerInterceptor {

    private final ThemeConfig themeConfig;

    public ThemeInterceptor(ThemeConfig themeConfig) {
        this.themeConfig = themeConfig;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                          Object handler, ModelAndView modelAndView) {
        if (modelAndView != null) {
            modelAndView.addObject("theme", themeConfig);
        }
    }
}

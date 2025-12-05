package com.artivisi.accountingfinance.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to apply rate limiting on sensitive endpoints.
 * Runs before authentication to prevent brute force attacks.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = getClientIpAddress(request);

        // Apply rate limiting to login POST requests
        if ("/login".equals(path) && "POST".equalsIgnoreCase(method)) {
            if (!rateLimitService.isLoginAllowed(clientIp)) {
                log.warn("Rate limit exceeded for login from IP: {}", LogSanitizer.ipAddress(clientIp));

                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("text/html;charset=UTF-8");
                response.setHeader("Retry-After", String.valueOf(rateLimitService.getLoginResetSeconds(clientIp)));
                response.setHeader("X-RateLimit-Remaining", "0");
                response.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitService.getLoginResetSeconds(clientIp)));

                response.getWriter().write("""
                    <!DOCTYPE html>
                    <html>
                    <head><title>Too Many Requests</title></head>
                    <body>
                        <h1>Too Many Requests</h1>
                        <p>Terlalu banyak percobaan login. Silakan tunggu beberapa saat sebelum mencoba lagi.</p>
                        <p>Too many login attempts. Please wait before trying again.</p>
                    </body>
                    </html>
                    """);
                return;
            }

            // Add rate limit headers for successful requests
            response.setHeader("X-RateLimit-Remaining",
                    String.valueOf(rateLimitService.getLoginRemaining(clientIp)));
        }

        // Apply rate limiting to API endpoints
        if (path.startsWith("/api/")) {
            if (!rateLimitService.isApiAllowed(clientIp)) {
                log.warn("Rate limit exceeded for API from IP: {}", LogSanitizer.ipAddress(clientIp));

                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader("Retry-After", "60");
                response.getWriter().write("{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Get client IP address, considering reverse proxy headers.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        // Check X-Forwarded-For header (set by reverse proxies)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP (original client)
            return xForwardedFor.split(",")[0].trim();
        }

        // Check X-Real-IP header (nginx)
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // Fall back to remote address
        return request.getRemoteAddr();
    }
}

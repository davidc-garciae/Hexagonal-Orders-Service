package com.pragma.powerup.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(2) // Ejecutar DESPUÉS del JwtAuthenticationFilter
public class CorrelationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String requestId =
        Optional.ofNullable(request.getHeader("X-Request-Id")).orElse(UUID.randomUUID().toString());

    MDC.put("requestId", requestId);
    extractUserIdFromJwt();

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

  /** Extract userId from JWT authentication instead of headers */
  private void extractUserIdFromJwt() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null) {
        Object details = authentication.getDetails();
        if (details instanceof JwtAuthenticationFilter.AuthDetails authDetails) {
          String userId = authDetails.getUserId();
          if (userId != null && !userId.isBlank()) {
            MDC.put("userId", userId);
          }
        }
      }
    } catch (Exception e) {
      // Silently ignore JWT extraction errors for correlation
      // This is non-critical functionality
    }
  }
}

package com.pragma.powerup.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Authentication Filter - Unified Template Processes JWT tokens from Authorization header and
 * sets Spring Security context.
 *
 * <p>This implementation is standardized across all microservices.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Execute BEFORE other filters
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtValidator jwtValidator;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    log.info("JwtAuthenticationFilter executing for path: {}", request.getRequestURI());

    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      log.info("No JWT token found in Authorization header for path: {}", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(7);
    log.info("Found JWT token, attempting validation for path: {}", request.getRequestURI());

    try {
      if (jwtValidator.isValid(token)) {
        log.info("JWT token is VALID, extracting claims...");

        String userId = jwtValidator.extractUserId(token);
        String email = jwtValidator.extractEmail(token);
        String role = jwtValidator.extractRole(token);

        log.info("JWT processing - Email: {}, Role: {}, UserId: {}", email, role, userId);

        if (email != null && role != null) {
          log.info("Setting authentication - User: {}, Role: {}", email, role);

          UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(
                  email,
                  null,
                  Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

          log.info("Created authentication with authorities: {}", auth.getAuthorities());

          // Add additional information to context
          auth.setDetails(new AuthDetails(userId, email, role));
          SecurityContextHolder.getContext().setAuthentication(auth);
          log.info("Successfully set SecurityContext authentication");
        } else {
          log.info("Email or role is null - Email: {}, Role: {}", email, role);
        }
      } else {
        log.info("JWT token validation FAILED");
      }
    } catch (Exception e) {
      log.info("Invalid JWT token: {}", e.getMessage());
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  /** Helper class to store authentication details. */
  public static class AuthDetails {
    private final String userId;
    private final String email;
    private final String role;

    public AuthDetails(String userId, String email, String role) {
      this.userId = userId;
      this.email = email;
      this.role = role;
    }

    public String getUserId() {
      return userId;
    }

    public String getEmail() {
      return email;
    }

    public String getRole() {
      return role;
    }
  }
}

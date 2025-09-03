package com.pragma.powerup.application.util;

import com.pragma.powerup.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * JWT-Compatible Security Utils
 *
 * <p>Replacement for SecurityUtils that works correctly with JWT Authentication. This version
 * extracts user information from AuthDetails instead of using reflection.
 */
@Component
public class JwtSecurityUtils {

  /**
   * Extract user ID from the current JWT SecurityContext
   *
   * @return The user ID from the authenticated user
   */
  public Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new IllegalStateException("No authentication found in SecurityContext");
    }

    // Extract userId from AuthDetails (set by JwtAuthenticationFilter)
    Object details = authentication.getDetails();
    if (details instanceof JwtAuthenticationFilter.AuthDetails authDetails) {
      String userId = authDetails.getUserId();
      if (userId != null) {
        return Long.valueOf(userId);
      }
    }

    throw new IllegalStateException("Could not extract user ID from JWT authentication");
  }

  /**
   * Extract user role from the current JWT SecurityContext
   *
   * @return The user role from the authenticated user
   */
  public String getCurrentUserRole() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new IllegalStateException("No authentication found in SecurityContext");
    }

    // Extract role from AuthDetails (set by JwtAuthenticationFilter)
    Object details = authentication.getDetails();
    if (details instanceof JwtAuthenticationFilter.AuthDetails authDetails) {
      return authDetails.getRole();
    }

    throw new IllegalStateException("Could not extract user role from JWT authentication");
  }

  /**
   * Extract user email from the current JWT SecurityContext
   *
   * @return The user email from the authenticated user
   */
  public String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new IllegalStateException("No authentication found in SecurityContext");
    }

    // Extract email from AuthDetails (set by JwtAuthenticationFilter)
    Object details = authentication.getDetails();
    if (details instanceof JwtAuthenticationFilter.AuthDetails authDetails) {
      return authDetails.getEmail();
    }

    throw new IllegalStateException("Could not extract user email from JWT authentication");
  }
}

package com.pragma.powerup.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT Validator - Unified Template Validates and extracts information from JWT
 * tokens.
 *
 * <p>
 * This implementation is standardized across all microservices.
 */
@Component
public class JwtValidator {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidator.class);

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Validates JWT token and extracts claims.
     *
     * @param token JWT token to validate
     * @return Claims from the token
     * @throws Exception if token is invalid
     */
    public Claims validateAndExtractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    /**
     * Checks if JWT token is valid.
     *
     * @param token JWT token to validate
     * @return true if valid, false otherwise
     */
    public boolean isValid(String token) {
        try {
            validateAndExtractClaims(token);
            return true;
        } catch (Exception e) {
            logger.error("JWT Validation Error: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    /**
     * Extracts user ID from JWT token.
     *
     * @param token JWT token
     * @return User ID as string
     */
    public String extractUserId(String token) {
        Claims claims = validateAndExtractClaims(token);
        Object userIdClaim = claims.get("userId");
        return userIdClaim != null ? userIdClaim.toString() : null;
    }

    /**
     * Extracts email (subject) from JWT token.
     *
     * @param token JWT token
     * @return Email address
     */
    public String extractEmail(String token) {
        Claims claims = validateAndExtractClaims(token);
        return claims.getSubject();
    }

    /**
     * Extracts role from JWT token.
     *
     * @param token JWT token
     * @return User role
     */
    public String extractRole(String token) {
        Claims claims = validateAndExtractClaims(token);
        return claims.get("role", String.class);
    }
}

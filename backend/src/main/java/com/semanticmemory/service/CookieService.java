package com.semanticmemory.service;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Service for managing HTTP cookies for authentication tokens.
 * 
 * Provides secure cookie management for JWT access tokens and refresh tokens.
 * Implements security best practices:
 * - HttpOnly: Prevents JavaScript access (XSS protection)
 * - Secure: SSL-only in production (when SameSite=None)
 * - SameSite: Configurable for cross-origin requests
 * - Path: Root path for whole application
 * 
 * Cookie Configuration:
 * - Access token: Short-lived (default 24 hours)
 * - Refresh token: Long-lived (default 7 days)
 * 
 * @author Semantic Memory Team
 * @version 1.0
 * @since 2024
 * 
 * @see <a href="https://owasp.org/www-community/HttpOnly">OWASP HttpOnly</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie">MDN Set-Cookie</a>
 */
@Service
public class CookieService {

    /** Logger for debugging and security monitoring */
    private static final Logger logger = LoggerFactory.getLogger(CookieService.class);

    /** JWT token expiration time in milliseconds (configured via jwt.expiration) */
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /** Access token cookie name (configured via app.cookie.name) */
    @Value("${app.cookie.name:ACCESS_TOKEN}")
    private String accessTokenCookieName;

    /** Refresh token cookie name (configured via app.cookie.refresh-name) */
    @Value("${app.cookie.refresh-name:REFRESH_TOKEN}")
    private String refreshTokenCookieName;

    /** SameSite attribute value (configured via app.cookie.same-site) */
    @Value("${app.cookie.same-site:None}")
    private String sameSite;

    /**
     * Sets the access token cookie in the response.
     * 
     * @param response HTTP response to add cookie to
     * @param token JWT access token value
     */
    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = createCookie(accessTokenCookieName, token, jwtExpiration / 1000);
        response.addHeader("Set-Cookie", cookie.toString());
        logger.debug("Access token cookie set with SameSite={}", sameSite);
    }

    /**
     * Sets the refresh token cookie in the response.
     * 
     * @param response HTTP response to add cookie to
     * @param token JWT refresh token value
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String token) {
        long refreshExpiration = jwtExpiration * 7;
        ResponseCookie cookie = createCookie(refreshTokenCookieName, token, refreshExpiration / 1000);
        response.addHeader("Set-Cookie", cookie.toString());
        logger.debug("Refresh token cookie set");
    }

    /**
     * Clears the access token cookie by setting empty value and maxAge=0.
     * 
     * @param response HTTP response to clear cookie in
     */
    public void clearAccessTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = createCookie(accessTokenCookieName, "", 0);
        response.addHeader("Set-Cookie", cookie.toString());
        logger.debug("Access token cookie cleared");
    }

    /**
     * Clears the refresh token cookie by setting empty value and maxAge=0.
     * 
     * @param response HTTP response to clear cookie in
     */
    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = createCookie(refreshTokenCookieName, "", 0);
        response.addHeader("Set-Cookie", cookie.toString());
        logger.debug("Refresh token cookie cleared");
    }

    /**
     * Creates a secure cookie with appropriate security attributes.
     * 
     * Security attributes:
     * - HttpOnly: Prevents XSS attacks from accessing token
     * - Secure: Required when SameSite=None for cross-origin
     * - SameSite: Controls cross-origin cookie sharing
     * - Path: Root path for application-wide access
     * - MaxAge: Cookie expiration time
     * 
     * @param name Cookie name
     * @param value Cookie value
     * @param maxAge Max age in seconds
     * @return Configured ResponseCookie
     */
    private ResponseCookie createCookie(String name, String value, long maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
            .httpOnly(true)  // Prevent JavaScript access (XSS protection)
            .path("/")       // Application-wide
            .maxAge(maxAge); // Expiration time
        
        // Configure SameSite and Secure based on environment
        // SameSite=None requires Secure=true for cross-origin requests
        if ("None".equals(sameSite)) {
            builder.sameSite("None").secure(true);
            logger.debug("Cookie {} set with SameSite=None (Secure)", name);
        } else {
            builder.sameSite("Lax").secure(false);
            logger.debug("Cookie {} set with SameSite=Lax", name);
        }
        
        return builder.build();
    }

    /**
     * Gets the access token cookie name.
     * 
     * @return Cookie name for access token
     */
    public String getAccessTokenCookieName() {
        return accessTokenCookieName;
    }

    /**
     * Gets the refresh token cookie name.
     * 
     * @return Cookie name for refresh token
     */
    public String getRefreshTokenCookieName() {
        return refreshTokenCookieName;
    }
}
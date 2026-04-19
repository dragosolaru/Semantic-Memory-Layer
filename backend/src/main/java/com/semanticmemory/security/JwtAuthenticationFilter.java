package com.semanticmemory.security;

import com.semanticmemory.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * JWT Authentication Filter for validating JWT tokens.
 * 
 * This filter runs on every request to authenticate users based on JWT tokens.
 * It supports two token sources:
 * 1. HTTP-only cookies (preferred for browser clients)
 * 2. Authorization header with Bearer token
 * 
 * Security measures:
 * - Tokens are validated using HMAC-SHA256
 * - User is loaded from database to get subscription tier for authorization
 * - Authentication is set only if not already present
 * 
 * @author Semantic Memory Team
 * @version 1.0
 * @since 2024
 * 
 * @see OncePerRequestFilter
 * @see <a href="https://jwt.io/">JWT.io</a>
 * @see <a href="https://docs.spring.io/spring-security/reference/6.5/servlet/authentication/index.html">Spring Security Authentication</a>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Logger for debugging and security monitoring */
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /** JWT secret key for token validation (configured via jwt.secret) */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /** Cookie name for access token (configured via app.cookie.name) */
    @Value("${app.cookie.name:ACCESS_TOKEN}")
    private String accessTokenCookieName;

    /** Repository for user data access */
    private final UserRepository userRepository;

    /**
     * Constructor injection for UserRepository.
     * 
     * @param userRepository Repository for accessing user data
     */
    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Processes each request to authenticate users via JWT.
     * 
     * Flow:
     * 1. Extract JWT from cookie or Authorization header
     * 2. Validate JWT signature and expiration
     * 3. Extract user ID from token subject
     * 4. Load user from database to get authorities
     * 5. Set authentication in SecurityContext
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain to continue processing
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Extract JWT token from cookie or header
        String jwt = extractTokenFromCookie(request);
        
        if (jwt == null) {
            jwt = extractTokenFromHeader(request);
        }

        String userId = null;

        // Validate JWT and extract user ID
        if (jwt != null) {
            try {
                // Parse and validate JWT token
                Claims claims = Jwts.parser()
                        .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                userId = claims.getSubject();
                logger.debug("JWT validated successfully for userId: {}", userId);
            } catch (Exception e) {
                // Log invalid token (don't expose details to client)
                logger.debug("Invalid JWT token: {}", e.getMessage());
            }
        }

        // Set authentication if user ID is valid and no authentication exists
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Setting authentication for userId: {}", userId);
            
            // Load user from database to get subscription tier for role-based access
            List<SimpleGrantedAuthority> authorities = userRepository.findById(UUID.fromString(userId))
                .map(user -> List.of(new SimpleGrantedAuthority("ROLE_" + user.getSubscriptionTier().name())))
                .orElse(List.of());

            // Create authentication token with user ID and authorities
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            // Set request details for security context
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from HTTP-only cookie.
     * 
     * @param request HTTP request
     * @return JWT token value or null if not found
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (accessTokenCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Extracts JWT token from Authorization header.
     * 
     * Expects format: "Bearer <token>"
     * 
     * @param request HTTP request
     * @return JWT token value or null if not found
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
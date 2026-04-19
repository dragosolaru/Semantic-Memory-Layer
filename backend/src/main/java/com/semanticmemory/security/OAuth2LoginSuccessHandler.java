package com.semanticmemory.security;

import com.semanticmemory.model.entity.User;
import com.semanticmemory.repository.UserRepository;
import com.semanticmemory.service.CookieService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * OAuth2 Login Success Handler for Google authentication.
 * 
 * Handles successful OAuth2 authentication from Google:
 * 1. Extracts user information from Google OAuth2 response
 * 2. Creates or updates user in database
 * 3. Generates JWT token for session management
 * 4. Sets secure HTTP-only cookie with JWT
 * 5. Redirects to frontend home page
 * 
 * Security measures:
 * - Validates email presence before account creation
 * - Prevents account takeover by checking provider
 * - Stores empty password for OAuth users (they can't login with password)
 * - Uses HTTP-only, Secure cookies for token storage
 * 
 * User data stored from Google:
 * - email: Primary email address
 * - name: Full name from Google profile
 * - picture: Profile picture URL
 * - sub: Google user ID (providerId)
 * 
 * @author Semantic Memory Team
 * @version 1.0
 * @since 2024
 * 
 * @see AuthenticationSuccessHandler
 * @see <a href="https://docs.spring.io/spring-security/reference/6.5/servlet/oauth2/login/core.html">OAuth2 Login</a>
 * @see <a href="https://developers.google.com/identity/protocols/oauth2">Google OAuth2</a>
 */
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    /** Logger for security event tracking */
    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    /** Repository for user data persistence */
    private final UserRepository userRepository;
    
    /** Service for secure cookie management */
    private final CookieService cookieService;

    /** JWT secret key for token signing (configured via jwt.secret) */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /** JWT token expiration in milliseconds (configured via jwt.expiration) */
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Constructor injection for dependencies.
     * 
     * @param userRepository Repository for user data access
     * @param cookieService Service for secure cookie management
     */
    public OAuth2LoginSuccessHandler(UserRepository userRepository, CookieService cookieService) {
        this.userRepository = userRepository;
        this.cookieService = cookieService;
    }

    /**
     * Handles successful OAuth2 authentication.
     * 
     * Process:
     * 1. Extract user attributes from OAuth2 provider (Google)
     * 2. Validate required fields (email is mandatory)
     * 3. Check for existing user account
     * 4. Create new user or update existing based on provider
     * 5. Generate JWT token with user claims
     * 6. Set secure HTTP-only cookie
     * 7. Redirect to frontend application
     * 
     * Error handling:
     * - no_email: Google didn't provide email address
     * - email_exists: Email already registered with different provider
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param authentication Authentication object from Spring Security
     * @throws IOException if redirect fails
     * @throws ServletException if servlet error occurs
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // Extract user information from Google OAuth2 response
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String provider = "google";
        String providerId = oAuth2User.getAttribute("sub");

        logger.info("OAuth2 login attempt for email: {}", email);

        // Validate required email field
        if (email == null) {
            logger.warn("OAuth2 login failed: No email provided by Google");
            response.sendRedirect("http://localhost:3000/login?error=no_email");
            return;
        }

        // Check for existing user in database
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            
            // Security: Prevent account takeover from different provider
            // If user registered with local credentials, reject OAuth login
            if (user.getProvider() != null && !provider.equals(user.getProvider())) {
                logger.warn("OAuth2 login failed: Email {} exists with different provider", email);
                response.sendRedirect("http://localhost:3000/login?error=email_exists");
                return;
            }
            
            // Update provider info and last login
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setLastLoginAt(LocalDateTime.now());
            
            // Set profile picture only if not already set
            if (picture != null && user.getProfileImageUrl() == null) {
                user.setProfileImageUrl(picture);
            }
            
            logger.info("Updated existing user: {} via Google OAuth2", user.getId());
        } else {
            // Create new user from OAuth2 data
            String[] nameParts = name != null ? name.split(" ", 2) : new String[2];
            user = User.builder()
                    .email(email)
                    .password("")  // OAuth users have no password
                    .provider(provider)
                    .providerId(providerId)
                    .firstName(nameParts.length > 0 ? nameParts[0] : null)
                    .lastName(nameParts.length > 1 ? nameParts[1] : null)
                    .profileImageUrl(picture)
                    .subscriptionTier(User.SubscriptionTier.FREE)
                    .lastLoginAt(LocalDateTime.now())
                    .build();
            
            logger.info("Created new user: {} via Google OAuth2", email);
        }

        // Save user to database
        user = userRepository.save(user);

        // Generate JWT token for session management
        String token = generateToken(user.getId().toString(), user.getEmail());
        
        // Set secure HTTP-only cookie with JWT
        cookieService.setAccessTokenCookie(response, token);

        logger.info("User {} logged in successfully via Google OAuth2", user.getEmail());

        // Redirect to frontend home page
        response.sendRedirect("http://localhost:3000/home");
    }

    /**
     * Generates a JWT token for the authenticated user.
     * 
     * Token contains:
     * - subject: User ID
     * - claim: Email address
     * - issuedAt: Token creation time
     * - expiration: Token expiry time
     * 
     * @param userId User's UUID
     * @param email User's email address
     * @return Signed JWT token string
     */
    private String generateToken(String userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
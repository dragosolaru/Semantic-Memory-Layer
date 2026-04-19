package com.semanticmemory.config;

import com.semanticmemory.security.JwtAuthenticationFilter;
import com.semanticmemory.security.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Security configuration for the Semantic Memory application.
 * 
 * Implements security best practices according to Spring Security 6.x:
 * - Stateless JWT-based authentication
 * - OAuth2 Login with Google
 * - CORS configuration for frontend communication
 * - CSRF disabled (stateless API with JWT)
 * - Session management set to STATELESS
 * 
 * Security Architecture:
 * - Public endpoints: /api/health, /api/auth/login, /api/auth/register, /api/uploads/**
 * - Protected endpoints: /api/auth/**, /api/search/**
 * - OAuth2 endpoints: /login/oauth2/**, /oauth2/**
 * 
 * @author Semantic Memory Team
 * @version 1.0
 * @since 2024
 * 
 * @see <a href="https://docs.spring.io/spring-security/reference/6.5/servlet/exploits/csrf.html">Spring Security CSRF</a>
 * @see <a href="https://docs.spring.io/spring-security/reference/6.5/servlet/oauth2/login/core.html">OAuth2 Login</a>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Allowed CORS origins (configured via app.cors.allowed-origins) */
    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    /** JWT authentication filter for validating tokens */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /** OAuth2 login success handler for Google authentication */
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    /**
     * Constructor injection for security components.
     * 
     * @param jwtAuthenticationFilter Filter for JWT token validation
     * @param oAuth2LoginSuccessHandler Handler for successful OAuth2 login
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, 
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    /**
     * Configures the main security filter chain.
     * 
     * Security measures implemented:
     * - CSRF: Disabled (stateless JWT authentication)
     * - CORS: Enabled with configurable origins
     * - Sessions: STATELESS (no server-side sessions)
     * - JWT Filter: Added before UsernamePasswordAuthenticationFilter
     * - OAuth2: Configured with custom success handler
     * - Authorization: Endpoint-based access control
     * 
     * @param http HttpSecurity object to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF disabled - using stateless JWT authentication
            // For SPAs, consider using CookieCsrfTokenRepository if forms are used
            .csrf(csrf -> csrf.disable())
            
            // CORS configuration for cross-origin requests
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Stateless session management - no session will be created
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Add JWT filter before Spring Security's default authentication filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // OAuth2 Login configuration with Google
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2LoginSuccessHandler)
            )
            
            // Custom authentication entry point for 401 responses
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                })
            )
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/refresh").permitAll()
                .requestMatchers("/api/uploads/**").permitAll()
                .requestMatchers("/login/oauth2/**").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/home").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Protected endpoints - authentication required
                .requestMatchers("/api/auth/**").authenticated()
                .requestMatchers("/api/search/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );
        return http.build();
    }

    /**
     * CORS configuration source.
     * 
     * Configures:
     * - Allowed origins from properties
     * - Allowed HTTP methods
     * - Allowed headers (all)
     * - Exposed headers for authentication
     * - Credentials allowed
     * - Preflight cache duration (1 hour)
     * 
     * @return CorsConfigurationSource for Spring Security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins from configuration
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        
        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allowed headers (all for API)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Exposed headers for authentication
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Preflight cache duration (1 hour)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Password encoder bean using BCrypt.
     * 
     * BCrypt is a slow hashing algorithm designed to resist brute-force attacks.
     * Default strength is 10 rounds, which provides good security.
     * 
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
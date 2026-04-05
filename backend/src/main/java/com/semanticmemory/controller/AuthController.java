package com.semanticmemory.controller;

import com.semanticmemory.model.dto.*;
import com.semanticmemory.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller.
 * 
 * Handles:
 * - Login (public)
 * - Registration (public)
 * - Change password (protected)
 * - Logout (protected)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * User login.
     * 
     * @param request Login credentials
     * @return JWT token and user data
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    /**
     * User registration.
     * 
     * @param request Registration data
     * @return JWT token and user data
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Change user password.
     * 
     * Requires authentication.
     * Current password must be verified.
     * 
     * @param request Password change data
     * @return Success message
     */
    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }

    /**
     * User logout.
     * 
     * Currently a no-op since JWT is stateless.
     * Could be used to blacklist the token in the future.
     * 
     * @return Success message
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }
}
package com.semanticmemory.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank
    private String email;
    
    @NotBlank
    private String currentPassword;
    
    @NotBlank
    private String newPassword;
}
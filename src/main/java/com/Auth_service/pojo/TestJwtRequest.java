package com.Auth_service.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestJwtRequest {
    private String sub;          // Subject (typically "JWTAUth")
    private String role;         // User role (e.g., "USER", "ADMIN")
    private String iss;          // Issuer (typically "auth-service")
    private boolean password_reset_token;  // Is this a password reset token
    private Long ID;             // User ID
}


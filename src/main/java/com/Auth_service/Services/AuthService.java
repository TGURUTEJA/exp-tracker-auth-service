package com.Auth_service.Services;

import java.util.ArrayList;
import java.util.Arrays;

import com.Auth_service.pojo.AuthResponse;
import com.Auth_service.pojo.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.Auth_service.RequestHandler.RequestHandler;
import com.Auth_service.pojo.AuthRequest;

@Component
public class AuthService {
    
    @Autowired
    RequestHandler requestHandler;


    public ResponseEntity<AuthResponse> registerUser(RegisterRequest request) {
        return requestHandler.RegisterHandler(request);
    }

    public ResponseEntity<AuthResponse> loginUser(AuthRequest userCred) {
        return requestHandler.loginHandler(userCred);
    }

    public ResponseEntity<AuthResponse> logoutUser() {
        // Invalidate the token (if stored server-side) and clear the cookie
        ResponseCookie deleteCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)            // set false only for local HTTP dev
                .path("/")
                .maxAge(0)               // Expire immediately
                .sameSite("Strict")      // or "Lax" if needed for cross-site
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(new AuthResponse(false,new ArrayList<>(), "Logged out successfully",null));
    }

    public ResponseEntity<AuthResponse> forgotPassword(String email, String username) {
       return requestHandler.forgotPasswordHandler(email,username);
    }

    public ResponseEntity<AuthResponse> resetPassword(String OTP, String newPassword, HttpServletRequest request) {
        String token = getAccessTokenFromCookies(request);
        if (token == null) {
            System.out.println("Missing or invalid access token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return requestHandler.resetPassword(OTP,newPassword, token);
    }


    public ResponseEntity<String> verifyEmail(String token) {
        return requestHandler.verifyEmail(token);
    }

    public ResponseEntity<AuthResponse> sendVerificationEmail(String email) {
        return requestHandler.sendVerificationEmail(email);
    }

    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        String token = getAccessTokenFromCookies(request);
        if (token == null) {
            System.out.println("Missing or invalid access token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return requestHandler.refreshToken(token);
    }
    // helper to cookie-extraction logic
    private String getAccessTokenFromCookies(HttpServletRequest request) {
        if (request == null || request.getCookies() == null) {
            return null;
        }
        jakarta.servlet.http.Cookie cookie = Arrays.stream(request.getCookies())
                .filter(c -> "access_token".equals(c.getName()))
                .findFirst()
                .orElse(null);

        if (cookie == null || cookie.getValue() == null || cookie.getValue().isBlank()) {
            return null;
        }
        return cookie.getValue();
    }


}

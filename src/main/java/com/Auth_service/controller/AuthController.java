package com.Auth_service.controller;

import java.util.Map;
import com.Auth_service.pojo.AuthResponse;
import com.Auth_service.pojo.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Auth_service.Services.AuthService;
import com.Auth_service.pojo.AuthRequest;

@RestController
@RequestMapping("/api/auth") // normalized base path
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/status")
    public String status() {
        return "Auth Service is running";
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return authService.loginUser(authRequest);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody Object request) {
        return authService.logoutUser();
    }

    @PostMapping(value = "/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String Username = request.get("userName");
        return authService.forgotPassword(email, Username);
    }

    @PostMapping(value = "/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(HttpServletRequest request,
            @RequestBody Map<String, String> requestBody) {
        String newPassword = requestBody.get("newPassword");
        String OTP = requestBody.get("OTP");
        return authService.resetPassword(OTP,newPassword, request);
    }
    //method to send verification email
    @PostMapping(value = "/Email-verify")
    public ResponseEntity<AuthResponse> sendVerificationEmail(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        return authService.sendVerificationEmail(email);
    }

    @GetMapping(value = "/Email-verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        System.out.println(token);
        return authService.verifyEmail(token);
    }


    @GetMapping(value = "/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        return authService.refreshToken(request);
    }

//    @PostMapping("/deleate-account")
//    public ResponseEntity<AuthResponse> deleteAccount(HttpServletRequest request, @RequestBody Map<String, String> requestBody) {
//        String password = requestBody.get("password");
//        return authService.deleteAccount(request, password);
//    }


}

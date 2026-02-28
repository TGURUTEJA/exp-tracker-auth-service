package com.Auth_service.RequestHandler;


import com.Auth_service.pojo.*;
import com.nimbusds.jwt.JWTParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class RequestHelper {

    private final JwtEncoder jwtEncoder;

    public AuthResponse authErrorResponse(String message, List<CheckMessage> errorData) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setError(true);
        authResponse.setMessage(message);
        authResponse.setID(null);
        authResponse.setErrorData(errorData);
        return authResponse;
    }

    public AuthResponse authSuccessResponse(String message, Long ID) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setError(false);
        authResponse.setMessage(message);
        authResponse.setID(ID);
        return authResponse;
    }

    public List<CheckMessage> getRegisterCheckMessages(RegisterRequest request) {
        List<CheckMessage> errorData = new ArrayList<>();

        if (isBlank(request.getPassword())) {
            errorData.add(new CheckMessage(true, "Password", "Password cannot be empty"));
        }
        if (isBlank(request.getUserName())) {
            errorData.add(new CheckMessage(true, "UserName", "UserName cannot be empty"));
        }
        if (isBlank(request.getEmail())) {
            errorData.add(new CheckMessage(true, "Email", "Email cannot be empty"));
        }
        return errorData;

    }

    public List<CheckMessage> getLoginCheckMessages(AuthRequest request) {
        List<CheckMessage> errorData = new ArrayList<>();

        if (isBlank(request.getPassword())) {
            errorData.add(new CheckMessage(true, "Password", "Password cannot be empty"));
        }
        if (isBlank(request.getUserName()) && isBlank(request.getEmail())) {
            errorData.add(new CheckMessage(true, "UserName/Email", "UserName or Email must be provided"));
        }
        return errorData;

    }

    public boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }



    public ResponseEntity<AuthResponse> responseWithJWT(AuthResponse response, String role, boolean password_reset_token){
        String subject = "JWTAUth";
        Long Id = response.getID();
        Instant now = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(subject);
        claims.claim("ID", Id);
        claims.claim("role", role);
        claims.claim("password_reset_token", password_reset_token);
        JwsHeader header = JwsHeader.with(() -> "RS256").build();
        String jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
        ResponseCookie jwtCookie = ResponseCookie.from("access_token", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .body(response);
    }

    public String generateOTPToken(Long ID, boolean path) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .subject("OTPToken");
        claims.claim("ID", ID);
        claims.claim("password_reset_token", path);
        JwsHeader header = JwsHeader.with(() -> "RS256").build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }

    public JwtResponse validateJWT(String token) {
        try {
            var jwt = JWTParser.parse(token);
            var claims = jwt.getJWTClaimsSet();

            Long idObj = (Long) claims.getClaim("ID");
            boolean Path = (boolean) claims.getClaim("password_reset_token");
            Date expireTime = claims.getExpirationTime();
            String role = (String) claims.getClaim("role");
            if (idObj == null) {
                return new JwtResponse(true, "Missing required claims", null, expireTime, Path, role);
            }
            return new JwtResponse(false, "Token is valid", idObj, expireTime, Path, role);
        } catch (Exception e) {
            return new JwtResponse(true, "Invalid or expired JWT: " + e.getMessage(), null, null, false, null);
        }
    }

    public ResponseEntity<String> responseWithHTMLMessage(String message){
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(
                        "<!DOCTYPE html>" +
                                "<html lang=\"en\">" +
                                "<head>" +
                                "<meta charset=\"UTF-8\">" +
                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                                "<title>Notification</title>" +
                                "<style>" +
                                "body { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; margin: 0; display: flex; align-items: center; justify-content: center; font-family: 'Segoe UI', Arial, sans-serif; }" +
                                ".card { background: #fff; border-radius: 16px; box-shadow: 0 8px 32px rgba(44,62,80,0.15); padding: 2.5rem 2rem; max-width: 400px; width: 100%; text-align: center; }" +
                                ".card h1 { color: #4f3ca7; margin-bottom: 1rem; font-size: 2rem; }" +
                                ".card p { color: #333; font-size: 1.1rem; margin-bottom: 1.5rem; }" +
                                ".btn { display: inline-block; padding: 0.7rem 1.5rem; background: #667eea; color: #fff; border-radius: 8px; text-decoration: none; font-weight: 500; transition: background 0.2s; }" +
                                ".btn:hover { background: #4f3ca7; }" +
                                "</style>" +
                                "</head>" +
                                "<body>" +
                                "<div class=\"card\">" +
                                "<h1>Notification</h1>" +
                                "<p>" + message + "</p>" +
                                "<a href=\"http://localhost:3030/login\" class=\"btn\">Go Home</a>" +
                                "</div>" +
                                "</body>" +
                                "</html>"
                );
    }
    public Long asLong(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException ex) {
                System.out.println("Failed to parse Long from String: " + ex.getMessage());
            }
        }
        return null;
    }



}

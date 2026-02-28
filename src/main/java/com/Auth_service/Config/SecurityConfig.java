package com.Auth_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS at the security layer
            .cors(c -> c.configurationSource(corsConfigurationSource()))
            // Disable CSRF for stateless API; if you need CSRF for forms, configure accordingly
            .csrf(csrf -> csrf.disable())
            // Public endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/api/auth/login", "/api/auth/register", "/api/auth/status","/api/auth/logout","/api/auth/forgot-password","/api/auth/verify-otp","/api/auth/reset-password","/api/auth/Email-verify","/api/auth/refresh-token").permitAll()
                .anyRequest().authenticated()
            )
            // Disable form login and HTTP Basic for API (optional; keep if you need)
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Exact UI origin; add others if needed (e.g., http://127.0.0.1:3030)
        config.setAllowedOrigins(List.of("http://localhost:3030"));

        // Methods your API supports
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers allowed from the browser
        config.setAllowedHeaders(List.of("Content-Type", "Authorization"));

        // If you use cookies/session or send Authorization headers
        config.setAllowCredentials(true);

        // Cache preflight for 10 minutes
        config.setMaxAge(600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply to your API paths
        source.registerCorsConfiguration("/api/**", config);
        // If you also need it for root or other paths, add them similarly
        return source;
    }
}

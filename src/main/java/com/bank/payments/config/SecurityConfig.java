package com.bank.payments.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth 2.0 Security Configuration
 * 
 * Features:
 * - JWT validation (signature, issuer, audience, expiration)
 * - Stateless (no HTTP sessions)
 * - Public endpoints: /actuator/health
 * - Protected endpoints: /api/**
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.audiences}")
    private String audience;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Stateless API
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // Protected endpoints - require authentication
                .requestMatchers("/api/**").authenticated()
                .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );
        
        return http.build();
    }

    /**
     * Configure JWT decoder with custom validations
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String issuerUri = System.getenv("OAUTH_ISSUER_URI");
        
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        // Audience validation
        OAuth2TokenValidator<Jwt> audienceValidator = 
            new JwtClaimValidator<String>("aud", aud -> aud.equals(audience));

        // Issuer validation
        OAuth2TokenValidator<Jwt> withIssuer = 
            JwtValidators.createDefaultWithIssuer(issuerUri);

        // Combine validators
        OAuth2TokenValidator<Jwt> withAudienceAndIssuer = 
            new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudienceAndIssuer);

        return jwtDecoder;
    }
}

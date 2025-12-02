package com.bank.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Payments Service - Secure Payment Processing Microservice
 * 
 * Features:
 * - OAuth 2.0 authentication with JWT validation
 * - Automatic JWT signature and claims validation
 * - Scope-based authorization
 * 
 * @author Architecture Team
 * @version 1.0.0
 */
@SpringBootApplication
public class PaymentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentsApplication.class, args);
        System.out.println("""
            
            ========================================
            🚀 Payments Service Started
            ========================================
            OAuth 2.0: ✅ Enabled
            JWT Validation: ✅ Enabled
            
            Endpoints:
            - GET  /api/payments/balance
            - POST /api/payments/transfer
            - GET  /actuator/health
            ========================================
            """);
    }
}

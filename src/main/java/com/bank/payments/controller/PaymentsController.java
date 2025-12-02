package com.bank.payments.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.bank.payments.model.BalanceResponse;
import com.bank.payments.model.TransferRequest;
import com.bank.payments.model.TransferResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Payments Controller
 * 
 * OAuth 2.0 protected endpoints:
 * - GET /api/payments/balance - Requires scope: payments.read
 * - POST /api/payments/transfer - Requires scope: payments.write
 */
@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentsController {

    /**
     * Get account balance
     * Required scope: api://payments-service/read
     */
    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('SCOPE_api://payments-service/read')")
    public ResponseEntity<BalanceResponse> getBalance(
            @AuthenticationPrincipal Jwt jwt) {
        
        log.info("Balance request from user: {}", jwt.getSubject());
        
        // Extract info from JWT
        String userId = jwt.getSubject();
        String userName = jwt.getClaimAsString("name");
        
        // Business logic simulation
        BalanceResponse response = BalanceResponse.builder()
            .userId(userId)
            .userName(userName)
            .accountNumber("4532-1234-5678-9012")
            .balance(new BigDecimal("15000.50"))
            .currency("USD")
            .lastUpdate(Instant.now())
            .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Execute transfer
     * Required scope: api://payments-service/write
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('SCOPE_api://payments-service/write')")
    public ResponseEntity<TransferResponse> transfer(
            @RequestBody TransferRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        log.info("Transfer request from user: {} for amount: {}", 
            jwt.getSubject(), request.getAmount());
        
        // Basic validation
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        // Business logic simulation
        TransferResponse response = TransferResponse.builder()
            .transactionId("TXN-" + System.currentTimeMillis())
            .status("COMPLETED")
            .fromAccount(request.getFromAccount())
            .toAccount(request.getToAccount())
            .amount(request.getAmount())
            .currency("USD")
            .timestamp(Instant.now())
            .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get JWT token info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo(
            @AuthenticationPrincipal Jwt jwt) {
        
        Map<String, Object> info = new HashMap<>();
        info.put("authenticated", true);
        info.put("userId", jwt.getSubject());
        info.put("scopes", jwt.getClaimAsStringList("scp"));
        info.put("issuer", jwt.getIssuer());
        info.put("audience", jwt.getAudience());
        info.put("issuedAt", jwt.getIssuedAt());
        info.put("expiresAt", jwt.getExpiresAt());
        
        return ResponseEntity.ok(info);
    }
}

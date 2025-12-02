package com.bank.payments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    private String userId;
    private String userName;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private Instant lastUpdate;
}

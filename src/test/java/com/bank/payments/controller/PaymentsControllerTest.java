package com.bank.payments.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetBalance_WithValidToken_ReturnsBalance() throws Exception {
        mockMvc.perform(get("/api/payments/balance")
                .with(jwt()
                    .jwt(jwt -> jwt
                        .claim("scp", "api://payments-service/read")
                        .claim("name", "Test User")
                        .subject("user123")
                    )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.userName").value("Test User"))
                .andExpect(jsonPath("$.balance").exists());
    }

    @Test
    void testGetBalance_WithoutToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/payments/balance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetBalance_WithInvalidScope_Returns403() throws Exception {
        mockMvc.perform(get("/api/payments/balance")
                .with(jwt()
                    .jwt(jwt -> jwt
                        .claim("scp", "api://payments-service/write") // Wrong scope
                        .subject("user123")
                    )
                ))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTransfer_WithValidToken_ReturnsSuccess() throws Exception {
        String requestBody = """
            {
                "fromAccount": "4532-1234-5678-9012",
                "toAccount": "4532-9876-5432-1098",
                "amount": 100.00,
                "currency": "USD",
                "description": "Test transfer"
            }
            """;

        mockMvc.perform(post("/api/payments/transfer")
                .contentType("application/json")
                .content(requestBody)
                .with(jwt()
                    .jwt(jwt -> jwt
                        .claim("scp", "api://payments-service/write")
                        .subject("user123")
                    )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").exists())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}

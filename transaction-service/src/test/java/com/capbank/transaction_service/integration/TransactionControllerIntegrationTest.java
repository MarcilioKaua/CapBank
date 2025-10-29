package com.capbank.transaction_service.integration;

import com.capbank.transaction_service.infrastructure.dto.DepositRequest;
import com.capbank.transaction_service.infrastructure.dto.WithdrawalRequest;
import com.capbank.transaction_service.infrastructure.dto.TransferRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Transaction Controller Integration Tests")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create deposit successfully via POST /api/transactions/deposit")
    void shouldCreateDepositSuccessfully() throws Exception {
        
        DepositRequest request = new DepositRequest(
                "550e8400-e29b-41d4-a716-446655440001",
                new BigDecimal("100.00"),
                "Integration test deposit"
        );

       
        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction").exists())
                .andExpect(jsonPath("$.transaction.transaction_type").value("DEPOSIT"))
                .andExpect(jsonPath("$.transaction.target_account_id").value("550e8400-e29b-41d4-a716-446655440001"))
                .andExpect(jsonPath("$.transaction.amount").value(100.00))
                .andExpect(jsonPath("$.message").value("Deposit processed successfully. Amount: 100.00"))
                .andExpect(jsonPath("$.notification_sent").isBoolean());
    }

    @Test
    @DisplayName("Should create withdrawal successfully via POST /api/transactions/withdrawal")
    void shouldCreateWithdrawalSuccessfully() throws Exception {
      
        WithdrawalRequest request = new WithdrawalRequest(
                "550e8400-e29b-41d4-a716-446655440000",
                new BigDecimal("50.00"),
                "Integration test withdrawal"
        );

        mockMvc.perform(post("/api/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction").exists())
                .andExpect(jsonPath("$.transaction.transaction_type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.transaction.source_account_id").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.transaction.amount").value(50.00))
                .andExpect(jsonPath("$.message").value("Withdrawal processed successfully. Amount: 50.00"))
                .andExpect(jsonPath("$.notification_sent").isBoolean());
    }

    @Test
    @DisplayName("Should create transfer successfully via POST /api/transactions/transfer")
    void shouldCreateTransferSuccessfully() throws Exception {

        TransferRequest request = new TransferRequest(
                "550e8400-e29b-41d4-a716-446655440000",
                "550e8400-e29b-41d4-a716-446655440001",
                new BigDecimal("75.00"),
                "Integration test transfer"
        );

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction").exists())
                .andExpect(jsonPath("$.transaction.transaction_type").value("TRANSFER"))
                .andExpect(jsonPath("$.transaction.source_account_id").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.transaction.target_account_id").value("550e8400-e29b-41d4-a716-446655440001"))
                .andExpect(jsonPath("$.transaction.amount").value(75.00))
                .andExpect(jsonPath("$.message").value("Transfer processed successfully. Amount: 75.00"))
                .andExpect(jsonPath("$.notification_sent").isBoolean());
    }

    @Test
    @DisplayName("Should return 400 when deposit has blank target account")
    void shouldReturn400WhenDepositHasInvalidTargetAccount() throws Exception {
       
        String invalidRequest = """
                {
                    "target_account_id": "   ",
                    "amount": 100.00,
                    "description": "Invalid deposit"
                }
                """;

    
        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when withdrawal has invalid amount")
    void shouldReturn400WhenWithdrawalHasInvalidAmount() throws Exception {
        
        String invalidRequest = """
                {
                    "source_account_id": "550e8400-e29b-41d4-a716-446655440000",
                    "amount": -50.00,
                    "description": "Invalid withdrawal"
                }
                """;

        mockMvc.perform(post("/api/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when transfer has same source and target accounts")
    void shouldReturn400WhenTransferHasSameSourceAndTarget() throws Exception {
       
        String invalidRequest = """
                {
                    "source_account_id": "550e8400-e29b-41d4-a716-446655440000",
                    "target_account_id": "550e8400-e29b-41d4-a716-446655440000",
                    "amount": 100.00,
                    "description": "Invalid transfer"
                }
                """;

       
        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when deposit amount is null")
    void shouldReturn400WhenDepositAmountIsNull() throws Exception {
       
        String invalidRequest = """
                {
                    "target_account_id": "550e8400-e29b-41d4-a716-446655440001",
                    "description": "No amount"
                }
                """;

       
        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }
}

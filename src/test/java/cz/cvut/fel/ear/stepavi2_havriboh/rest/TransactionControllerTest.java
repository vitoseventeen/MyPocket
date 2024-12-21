package cz.cvut.fel.ear.stepavi2_havriboh.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.TransactionNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.TransactionType;
import cz.cvut.fel.ear.stepavi2_havriboh.main.rest.TransactionController;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.TransactionService;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest extends BaseControllerTestRunner {

    private TransactionService transactionService;
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        transactionService = Mockito.mock(TransactionService.class);
        transactionController = new TransactionController(transactionService);
        super.setUp(transactionController);
    }
    @Test
    void getTransactionById_shouldReturnTransaction() throws Exception {
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDate(LocalDate.of(2023, 12, 1));
        transaction.setDescription("Test Transaction");
        transaction.setType(TransactionType.EXPENSE);

        // Настройка мока
        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/transactions/{id}", transactionId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.amount").value(10))
                .andExpect(jsonPath("$.date").value("2023-12-01"))
                .andExpect(jsonPath("$.description").value("Test Transaction"))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }
    @Test
    void getTransactionById_shouldReturn404WhenNotFound() throws Exception {
        int transactionId = 999;
        doThrow(new TransactionNotFoundException("Transaction not found")).when(transactionService).getTransactionById(transactionId);

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/transactions/{id}", transactionId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Transaction not found with ID: " + transactionId + "\""));  // Ожидаем сообщение об ошибке
    }


    @Test
    void createTransaction_shouldCreateTransaction() throws Exception {

    }

    @Test
    void deleteTransaction_shouldDeleteTransaction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/transactions/1"))
                .andExpect(status().is(200))
                .andExpect(content().string("\"Transaction deleted\""));

        verify(transactionService, times(1)).deleteTransactionById(1);
    }

    @Test
    void deleteTransaction_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new TransactionNotFoundException("Transaction not found")).when(transactionService).deleteTransactionById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/transactions/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Transaction not found\""));
    }


    @Test
    void updateTransaction_shouldUpdateTransaction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/rest/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":10,\"date\":\"2023-12-01\",\"description\":\"Test Transaction\",\"type\":\"EXPENSE\",\"user\":{\"id\":1},\"category\":{\"id\":2},\"account\":{\"id\":3}}"))
                .andExpect(status().is(200))
                .andExpect(content().string("\"Transaction updated\""));

        verify(transactionService, times(1)).updateTransaction(
                eq(1),
                eq(BigDecimal.TEN),
                eq(LocalDate.of(2023, 12, 1)),
                eq("Test Transaction"),
                eq(TransactionType.EXPENSE)
        );
    }
}

package cz.cvut.sem.ear.stepavi2.havriboh.main.rest;

import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.CategoryNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.TransactionNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.TransactionType;
import cz.cvut.sem.ear.stepavi2.havriboh.main.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

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
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDate(LocalDate.now());
        transaction.setDescription("Test Transaction");
        transaction.setType(TransactionType.EXPENSE);

        when(transactionService.getTransactionsById(1)).thenReturn(transaction);

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/transactions/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.description").value("Test Transaction"));
    }


    @Test
    void getTotalSpentByCategoryId_shouldReturnTotalSpent() throws Exception {
        when(transactionService.getTotalSpentByCategoryId(1)).thenReturn(BigDecimal.valueOf(100));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/transactions/1/total"))
                .andExpect(status().is(200))
                .andExpect(content().string("100"));
    }

    @Test
    void getTotalSpentByCategoryId_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new CategoryNotFoundException("Category not found")).when(transactionService).getTotalSpentByCategoryId(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/transactions/1/total"))
                .andExpect(status().is(404))
                .andExpect(content().string("\"Category not found\""));
    }

    @Test
    void createTransaction_shouldCreateTransaction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/rest/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":10,\"date\":\"2023-12-01\",\"description\":\"Test Transaction\",\"type\":\"EXPENSE\",\"user\":{\"id\":1},\"category\":{\"id\":2},\"account\":{\"id\":3}}"))
                .andExpect(status().is(201))
                .andExpect(content().string("\"Transaction created\""));

        verify(transactionService, times(1)).createTransaction(
                eq(BigDecimal.TEN),
                eq(LocalDate.of(2023, 12, 1)),
                eq("Test Transaction"),
                eq(TransactionType.EXPENSE),
                eq(1),
                eq(3),
                eq(2)
        );
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

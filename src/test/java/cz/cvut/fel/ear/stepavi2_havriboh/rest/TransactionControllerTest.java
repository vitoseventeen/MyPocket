package cz.cvut.fel.ear.stepavi2_havriboh.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.TransactionNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Transaction;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.rest.TransactionController;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.Mockito.*;

class TransactionControllerTest extends BaseControllerTestRunner {

    private TransactionService transactionService;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    @BeforeEach
    void setUp() {
        transactionService = Mockito.mock(TransactionService.class);
        TransactionController transactionController = new TransactionController(transactionService);
        super.setUp(transactionController);

        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
        User mockUser = new User();
        mockUser.setRole(Role.ADMIN);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(mockUser);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void getAllTransactions_shouldReturnTransactions() throws Exception {
        Transaction transaction1 = new Transaction();
        transaction1.setAmount(new BigDecimal("100.00"));

        Transaction transaction2 = new Transaction();
        transaction2.setAmount(new BigDecimal("200.00"));

        when(transactionService.getAllTransactions()).thenReturn(Arrays.asList(transaction1, transaction2));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/transactions"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @Test
    void getTransactionById_shouldReturnTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccount(new Account());
        transaction.setAmount(new BigDecimal("100.00"));

        when(transactionService.getTransactionById(1)).thenReturn(transaction);

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/transactions/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(100.00));
    }

    @Test
    void getTransactionById_shouldReturn404WhenNotFound() throws Exception {
        when(transactionService.getTransactionById(1)).thenThrow(new TransactionNotFoundException("Transaction not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/transactions/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Transaction not found\""));
    }

    @Test
    void deleteTransaction_shouldDeleteAndReturn200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/transactions/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("\"Transaction deleted\""));

        verify(transactionService, times(1)).deleteTransactionById(1);
    }

    @Test
    void deleteTransaction_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new TransactionNotFoundException("Transaction not found"))
                .when(transactionService).deleteTransactionById(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/transactions/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Transaction not found\""));
    }

    @Test
    void updateTransaction_shouldReturn200() throws Exception {
        //TODO: implement
    }

    @Test
    void updateTransaction_shouldReturn404WhenNotFound() throws Exception {
        //TODO: implement
    }

}

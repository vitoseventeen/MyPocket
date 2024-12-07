package cz.cvut.sem.ear.stepavi2.havriboh.main.rest;

import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.AccountNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Account;
import cz.cvut.sem.ear.stepavi2.havriboh.main.service.AccountService;


public class AccountControllerTest extends BaseControllerTestRunner {
    private AccountController accountController;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = Mockito.mock(AccountService.class);
        accountController = new AccountController(accountService);
        super.setUp(accountController);
    }

    @Test
    void getAllAccounts_shouldReturnAccounts() throws Exception {
        Account account1 = new Account();
        account1.setAccountName("Account1");
        account1.setBalance(BigDecimal.TEN);
        account1.setCurrency("CZK");

        Account account2 = new Account();
        account2.setAccountName("Account2");
        account2.setBalance(BigDecimal.valueOf(20));
        account2.setCurrency("EUR");

        when(accountService.getAllAccounts()).thenReturn(Arrays.asList(account1, account2));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/accounts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].accountName").value("Account1"))
                .andExpect(jsonPath("$[1].accountName").value("Account2"));
    }

    // create account
    @Test
    void createAccount_shouldCreateAndReturn201() throws Exception {
        Account account = new Account();
        account.setAccountName("Account1");
        account.setBalance(BigDecimal.TEN);
        account.setCurrency("CZK");

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/accounts")
                .contentType("application/json")
                .content("{\"accountName\":\"Account1\",\"balance\":10,\"currency\":\"CZK\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("\"Account created\""));
    }

    // update account
    @Test
    void updateAccount_shouldUpdateAndReturn200() throws Exception {
        Account account = new Account();
        account.setAccountName("Account1");
        account.setBalance(BigDecimal.TEN);
        account.setCurrency("CZK");

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/accounts/1")
                .contentType("application/json")
                .content("{\"accountName\":\"Account1\",\"balance\":10,\"currency\":\"CZK\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("\"Account with id 1 updated\""));
    }

    // delete account
    @Test
    void deleteAccount_shouldDeleteAndReturn200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/accounts/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("\"Account deleted\""));
    }

    // delete account not found
    @Test
    void deleteAccount_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new AccountNotFoundException("")).when(accountService).deleteAccountById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/accounts/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Account not found\""));
    }
}

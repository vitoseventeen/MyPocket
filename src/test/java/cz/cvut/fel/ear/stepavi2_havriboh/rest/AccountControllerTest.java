package cz.cvut.fel.ear.stepavi2_havriboh.rest;


import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.AccountNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.rest.AccountController;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;



public class AccountControllerTest extends BaseControllerTestRunner {
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = Mockito.mock(AccountService.class);
        AccountController accountController = new AccountController(accountService);
        super.setUp(accountController);
    }

    @Test
    void getAllAccounts_shouldReturnAccounts() throws Exception {

    }

    // create account
    @Test
    void createAccount_shouldCreateAndReturn201() throws Exception {

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

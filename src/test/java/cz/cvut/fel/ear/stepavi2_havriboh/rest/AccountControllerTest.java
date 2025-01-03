package cz.cvut.fel.ear.stepavi2_havriboh.rest;


import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.AccountNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.rest.AccountController;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role.ADMIN;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;



public class AccountControllerTest extends BaseControllerTestRunner {
    private AccountService accountService;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    @BeforeEach
    void setUp() {
        accountService = Mockito.mock(AccountService.class);
        AccountController accountController = new AccountController(accountService);
        super.setUp(accountController);


        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
        User mockUser = new User();
        mockUser.setRole(ADMIN);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(mockUser);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void getAllAccounts_shouldReturnAccounts() throws Exception {
        Account account1 = new Account();
        account1.setName("Account 1");

        Account account2 = new Account();
        account2.setName("Account 2");

        when(accountService.getAllAccounts()).thenReturn(Arrays.asList(account1, account2));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/accounts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Account 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Account 2"));
    }

    @Test
    void getAccountById_shouldReturnAccount() throws Exception {
        Account account = new Account();
        account.setName("Test Account");
        account.setCreator(SecurityUtils.getCurrentUser());

        User mockUser = new User();
        mockUser.setRole(ADMIN);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(mockUser);

        when(accountService.getAccountById(1)).thenReturn(account);

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/accounts/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Account"));
    }

    @Test
    void getAccountById_shouldReturn404WhenNotFound() throws Exception {
        when(accountService.getAccountById(1)).thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/accounts/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Account not found\""));
    }

}

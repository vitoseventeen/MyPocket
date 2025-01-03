package cz.cvut.fel.ear.stepavi2_havriboh.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.BudgetNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Currency;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.rest.BudgetController;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.BudgetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;

import static cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role.ADMIN;
import static org.mockito.Mockito.*;

public class BudgetControllerTest extends BaseControllerTestRunner {

    private BudgetService budgetService;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    @BeforeEach
    void setUp() {
        budgetService = Mockito.mock(BudgetService.class);
        BudgetController budgetController = new BudgetController(budgetService);
        super.setUp(budgetController);

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
    void getAllBudgets_shouldReturnBudgets() throws Exception {
        Budget budget1 = new Budget();
        budget1.setCurrency(Currency.USD);

        Budget budget2 = new Budget();
        budget2.setCurrency(Currency.CZK);

        when(budgetService.getAllBudgets()).thenReturn(Arrays.asList(budget1, budget2));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/budgets"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].currency").value("USD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].currency").value("CZK"));
    }


    @Test
    void getBudgetById_shouldReturn404WhenNotFound() throws Exception {
        when(budgetService.getBudgetById(1)).thenThrow(new BudgetNotFoundException("Budget not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/budgets/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget not found\""));
    }

    @Test
    void increaseBudget_shouldIncreaseAndReturn200() throws Exception {
        Budget budget = new Budget();
        budget.setCurrency(Currency.USD);
        budget.setBalance(BigDecimal.valueOf(0));
        budget.setAccount(new Account());

        when(budgetService.getBudgetById(1)).thenReturn(budget);
        doNothing().when(budgetService).increaseBudget(1, BigDecimal.valueOf(100), "USD");

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/budgets/1/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100,\"currency\":\"USD\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget increased\""));
    }

    @Test
    void increaseBudget_shouldReturn400ForNegativeAmount() throws Exception {
        Budget budget = new Budget();
        budget.setCurrency(Currency.USD);
        budget.setBalance(BigDecimal.valueOf(0));
        budget.setAccount(new Account());

        when(budgetService.getBudgetById(1)).thenReturn(budget);
        doThrow(new NegativeAmountException("Negative amount not allowed"))
                .when(budgetService).increaseBudget(1, BigDecimal.valueOf(-100), "USD");

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/budgets/1/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":-100,\"currency\":\"USD\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("\"Error increasing budget: Negative amount not allowed\""));
    }

    @Test
    void decreaseBudget_shouldDecreaseAndReturn200() throws Exception {
        Budget budget = new Budget();
        budget.setCurrency(Currency.USD);
        budget.setAccount(new Account());

        when(budgetService.getBudgetById(1)).thenReturn(budget);
        doNothing().when(budgetService).decreaseBudget(1, BigDecimal.valueOf(50), "USD");

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/budgets/1/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":50,\"currency\":\"USD\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget decreased\""));
    }

    @Test
    void decreaseBudget_shouldReturn400ForNegativeAmount() throws Exception {
        Budget budget = new Budget();
        budget.setCurrency(Currency.USD);
        budget.setBalance(BigDecimal.valueOf(100));
        budget.setAccount(new Account());

        when(budgetService.getBudgetById(1)).thenReturn(budget);
        doThrow(new NegativeAmountException("Negative amount not allowed"))
                .when(budgetService).decreaseBudget(1, BigDecimal.valueOf(-50), "USD");

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/budgets/1/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":-50,\"currency\":\"USD\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("\"Error decreasing budget: Negative amount not allowed\""));
    }
}

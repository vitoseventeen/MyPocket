package cz.cvut.sem.ear.stepavi2.havriboh.main.rest;

import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.BudgetNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Budget;
import cz.cvut.sem.ear.stepavi2.havriboh.main.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class BudgetControllerTest extends BaseControllerTestRunner {
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetService = Mockito.mock(BudgetService.class);
        BudgetController budgetController = new BudgetController(budgetService);
        super.setUp(budgetController);
    }

    @Test
    void getAllBudgets_shouldReturnAllBudgets() throws Exception {
        Budget budget1 = new Budget();
        budget1.setCategory(null);
        budget1.setCurrency("CZK");
        budget1.setTargetAmount(BigDecimal.valueOf(10000));
        budget1.setCurrentAmount(BigDecimal.ZERO);

        Budget budget2 = new Budget();
        budget2.setCategory(null);
        budget2.setCurrency("USD");
        budget2.setTargetAmount(BigDecimal.TEN);
        budget2.setCurrentAmount(BigDecimal.ZERO);

        when(budgetService.getAllBudgets()).thenReturn(Arrays.asList(budget1,budget2));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/budgets"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].currency").value("CZK"))
                .andExpect(jsonPath("$[1].currency").value("USD"));
    }

    @Test
    void getBudgetById_shouldReturnBudget() throws Exception {
        Budget budget = new Budget();
        budget.setCurrency("CZK");
        budget.setTargetAmount(BigDecimal.valueOf(5000));
        budget.setCurrentAmount(BigDecimal.ZERO);

        when(budgetService.getBudgetById(anyInt())).thenReturn(budget);

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/budgets/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.currency").value("CZK"))
                .andExpect(jsonPath("$.targetAmount").value(5000))
                .andExpect(jsonPath("$.currentAmount").value(0));
    }

    @Test
    void getBudgetById_shouldReturn404IfNotFound() throws Exception {
        when(budgetService.getBudgetById(anyInt())).thenThrow(new BudgetNotFoundException("Budget not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/budgets/999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget not found\""));
    }

    @Test
    void createBudget_shouldReturn201() throws Exception {
        String budgetJson = """
                {
                    "category": {"id": 1},
                    "currency": "EUR",
                    "targetAmount": 1000
                }
                """;

        doNothing().when(budgetService).createBudgetForCategoryById(anyInt(), any(BigDecimal.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(budgetJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget created\""));
    }

    @Test
    void createBudget_shouldReturn400IfInvalid() throws Exception {
        String budgetJson = """
                {
                    "category": {"id": 1},
                    "currency": "",
                    "targetAmount": -500
                }
                """;

        doThrow(new IllegalArgumentException("Invalid data")).when(budgetService)
                .createBudgetForCategoryById(anyInt(), any(BigDecimal.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(budgetJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("\"Error creating budget: Invalid data\""));
    }

    @Test
    void updateBudget_shouldReturn200() throws Exception {
        String budgetJson = """
                {
                    "targetAmount": 2000
                }
                """;

        doNothing().when(budgetService).increaseBudget(anyInt(), any(BigDecimal.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/budgets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(budgetJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget updated\""));
    }

    @Test
    void deleteBudget_shouldReturn200() throws Exception {
        doNothing().when(budgetService).deleteBudgetById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/budgets/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget deleted\""));
    }

    @Test
    void deleteBudget_shouldReturn404IfNotFound() throws Exception {
        doThrow(new BudgetNotFoundException("Budget not found")).when(budgetService).deleteBudgetById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/budgets/999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget not found\""));
    }

}

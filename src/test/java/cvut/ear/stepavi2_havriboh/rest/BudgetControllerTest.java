package cvut.ear.stepavi2_havriboh.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import cvut.ear.stepavi2_havriboh.main.dao.CategoryDao;
import cvut.ear.stepavi2_havriboh.main.exception.BudgetNotFoundException;
import cvut.ear.stepavi2_havriboh.main.exception.CategoryNotFoundException;
import cvut.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cvut.ear.stepavi2_havriboh.main.model.Budget;
import cvut.ear.stepavi2_havriboh.main.model.Category;
import cvut.ear.stepavi2_havriboh.main.rest.BudgetController;
import cvut.ear.stepavi2_havriboh.main.service.BudgetService;
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
    private CategoryDao categoryDao;


    @BeforeEach
    void setUp() {
        categoryDao = Mockito.mock(CategoryDao.class);
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
    void createBudget_shouldReturn400IfInvalid() throws Exception {
        Category category = new Category();
        category.setDefaultLimit(BigDecimal.valueOf(1000));
        category.setName("Food");
        category.setDescription("Food and drinks");

        when(categoryDao.find(1)).thenReturn(category);
        doThrow(new NegativeAmountException("Amount must be positive")).when(budgetService)
                .createBudgetForCategoryById(anyInt(), any(BigDecimal.class), anyString());

    }

    //TODO : FIX TESTS
    @Test
    void createBudget_shouldReturn201() throws Exception {
        Category category = new Category();
        category.setDefaultLimit(BigDecimal.valueOf(1000));
        category.setName("Food");
        category.setDescription("Food and drinks");

        // Мокаем поведение categoryDao
        when(categoryDao.find(1)).thenReturn(category);

        // Мокаем успешное создание бюджета
        doNothing().when(budgetService).createBudgetForCategoryById(anyInt(), any(BigDecimal.class), anyString());

        // JSON для тела запроса
        String budgetJson = """
            {
                "targetAmount": 1000,
                "currency": "CZK"
            }
            """;

        // Выполнение запроса и проверки
        mockMvc.perform(MockMvcRequestBuilders.post("/rest/budgets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(budgetJson))
                .andExpect(MockMvcResultMatchers.status().isCreated()) // Проверка статуса 201
                .andExpect(MockMvcResultMatchers.content().string("\"Budget created\"")); // Проверка содержимого ответа
    }



    @Test
    void increaseBudget_shouldReturn200() throws Exception {
        String budgetJson = """
                {
                    "targetAmount": 1000
                }
                """;

        doNothing().when(budgetService).increaseBudget(anyInt(), any(BigDecimal.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/budgets/1/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(budgetJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget increased successfully\""));
    }

    @Test
    void increaseBudget_shouldReturn400IfInvalid() throws Exception {
        String budgetJson = """
            {
                "targetAmount": -500
            }
            """;

        doThrow(new NegativeAmountException("Amount must be positive")).when(budgetService)
                .increaseBudget(anyInt(), any(BigDecimal.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/budgets/1/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(budgetJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("\"Error increasing budget: Amount must be positive\""));
    }

    @Test
    void decreaseBudget_shouldReturn200() throws Exception {
        String budgetJson = """
                {
                    "targetAmount": 1000
                }
                """;

        doNothing().when(budgetService).decreaseBudget(anyInt(), any(BigDecimal.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/budgets/1/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(budgetJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget decreased successfully\""));
    }

    @Test
    void decreaseBudget_shouldReturn400IfInvalid() throws Exception {
        String budgetJson = """
            {
                "targetAmount": -500
            }
            """;

        doThrow(new NegativeAmountException("Amount must be positive")).when(budgetService)
                .decreaseBudget(anyInt(), any(BigDecimal.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/budgets/1/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(budgetJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("\"Error decreasing budget: Amount must be positive\""));
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

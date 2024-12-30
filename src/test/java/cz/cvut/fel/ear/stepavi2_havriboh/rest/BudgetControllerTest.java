package cz.cvut.fel.ear.stepavi2_havriboh.rest;


import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.CategoryDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.BudgetNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.rest.BudgetController;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.BudgetService;
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

    }

    @Test
    void getBudgetById_shouldReturnBudget() throws Exception {
    }

    @Test
    void getBudgetById_shouldReturn404IfNotFound() throws Exception {
        when(budgetService.getBudgetById(anyInt())).thenThrow(new BudgetNotFoundException("Budget not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/budgets/999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Budget not found\""));
    }


    @Test
    void increaseBudget_shouldReturn200() throws Exception {

    }

    @Test
    void increaseBudget_shouldReturn400IfInvalid() throws Exception {

    }

    @Test
    void decreaseBudget_shouldReturn200() throws Exception {

    }

    @Test
    void decreaseBudget_shouldReturn400IfInvalid() throws Exception {

    }


//    @Test
//    void deleteBudget_shouldReturn200() throws Exception {
//        doNothing().when(budgetService).deleteBudgetById(anyInt());
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/budgets/1"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string("\"Budget deleted\""));
//    }
//
//    @Test
//    void deleteBudget_shouldReturn404IfNotFound() throws Exception {
//        doThrow(new BudgetNotFoundException("Budget not found")).when(budgetService).deleteBudgetById(anyInt());
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/budgets/999"))
//                .andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.content().string("\"Budget not found\""));
//    }

}

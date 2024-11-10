package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BudgetTest {

    private Budget budget;

    @BeforeEach
    void setUp() {
        budget = new Budget();
        budget.setTargetAmount(new BigDecimal("1000.00"));
        budget.setCurrentAmount(new BigDecimal("500.00"));
        budget.setCurrency("USD");
    }

    @Test
    void testAddBudget_validAmount() {
        BigDecimal amountToAdd = new BigDecimal("200.00");

        budget.addBudget(amountToAdd);

        assertEquals(new BigDecimal("700.00"), budget.getCurrentAmount(), "Current amount should be 700.00 after adding 200.00");
    }

    @Test
    void testAddBudget_exceedingTarget() {
        BigDecimal amountToAdd = new BigDecimal("600.00");

        budget.addBudget(amountToAdd);

        assertEquals(new BigDecimal("1100.00"), budget.getCurrentAmount(), "Current amount should be 1100.00 after adding 600.00");
    }

    @Test
    void testAddBudget_negativeAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            budget.addBudget(new BigDecimal("-100.00"));
        });
        assertEquals("Amount must be positive", exception.getMessage());
    }

    @Test
    void testRemoveBudget_validAmount() {
        BigDecimal amountToRemove = new BigDecimal("200.00");

        budget.removeBudget(amountToRemove);

        assertEquals(new BigDecimal("300.00"), budget.getCurrentAmount(), "Current amount should be 300.00 after removing 200.00");
    }

    @Test
    void testRemoveBudget_moreThanCurrent() {
        BigDecimal amountToRemove = new BigDecimal("600.00");

        budget.removeBudget(amountToRemove);

        assertEquals(BigDecimal.ZERO, budget.getCurrentAmount(), "Current amount should remain 500.00 after attempting to remove 600.00");
    }

    @Test
    void testRemoveBudget_negativeAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            budget.removeBudget(new BigDecimal("-100.00"));
        });
        assertEquals("Amount must be positive", exception.getMessage());
    }

    @Test
    void testCalculateRemainingBudget() {

        BigDecimal remaining = budget.calculateRemainingBudget();


        assertEquals(new BigDecimal("500.00"), remaining, "Remaining budget should be 500.00");
    }

}

package cz.cvut.fel.ear.stepavi2_havriboh.service;


import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.BudgetDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.TransactionDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Currency;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.AccountService;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.BudgetService;
import cz.cvut.fel.ear.stepavi2_havriboh.main.utils.CurrencyConverter;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class BudgetServiceTest {

    @Autowired
    private BudgetDao budgetDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private BudgetService budgetService;

    private Budget testBudget;

    private Budget testBudget2;
    @Autowired
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        testBudget = new Budget();
        testBudget.setBalance(BigDecimal.valueOf(1000));
        testBudget.setCurrency(Currency.valueOf("CZK"));
        budgetDao.persist(testBudget);

        testBudget2 = new Budget();
        testBudget2.setBalance(BigDecimal.valueOf(1000));
        testBudget2.setCurrency(Currency.valueOf("CZK"));
        budgetDao.persist(testBudget2);
    }

    @Test
    public void convertCurrencyConvertsCurrency() {
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal convertedAmount = CurrencyConverter.convert(amount, "USD", "EUR");
        assertEquals(BigDecimal.valueOf(90.0), convertedAmount);
    }


    @Test
    public void increaseBudgetIncreasesBudget() {
        int budgetId = testBudget.getId();
        BigDecimal initialBalance = testBudget.getBalance();
        BigDecimal amountToAdd = BigDecimal.valueOf(500);
        String currency = "CZK";

        budgetService.increaseBudget(budgetId, amountToAdd, currency);
        Budget updatedBudget = budgetDao.find(budgetId);
        assertNotNull(updatedBudget, "Budget should not be null after update");
        assertEquals(initialBalance.add(amountToAdd), updatedBudget.getBalance(), "The budget balance should be increased correctly");
    }

    @Test
    public void increaseBudgetThrowsNegativeAmountException() {
        assertThrows(NegativeAmountException.class, () ->
                budgetService.increaseBudget(1, BigDecimal.valueOf(-100), "USD")
        );
    }

    @Test
    public void getAllBudgetsReturnsAllBudgets() {
        List<Budget> budgets = budgetService.getAllBudgets();

        assertNotNull(budgets, "Budget list should not be null");
        assertEquals(2, budgets.size(), "Budget list should contain all persisted budgets");
        assertTrue(budgets.contains(testBudget), "Budget list should contain the first budget");
        assertTrue(budgets.contains(testBudget2), "Budget list should contain the second budget");

    }

    @Test
    public void decreaseBudgetThrowsNegativeAmountException() {
        assertThrows(NegativeAmountException.class, () ->
                budgetService.decreaseBudget(1, BigDecimal.valueOf(-100), "USD")
        );
    }


    @Test
    public void decreaseBudgetDecreasesBudget() {
        int budgetId = testBudget.getId();
        BigDecimal amountToDecrease = BigDecimal.valueOf(100);
        String currency = "CZK";

        budgetService.decreaseBudget(budgetId, amountToDecrease, currency);

        Budget updatedBudget = budgetDao.find(budgetId);
        assertEquals(BigDecimal.valueOf(900), updatedBudget.getBalance());
    }

}

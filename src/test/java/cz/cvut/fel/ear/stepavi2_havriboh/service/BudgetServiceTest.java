package cz.cvut.fel.ear.stepavi2_havriboh.service;


import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.BudgetDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.TransactionDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.BudgetNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.EmptyCurrencyException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
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
    @Autowired
    private AccountService accountService;


    @Test
    public void convertCurrencyConvertsCurrency() {
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal convertedAmount = CurrencyConverter.convert(amount, "USD", "EUR");
        assertEquals(BigDecimal.valueOf(90.0), convertedAmount);
    }


    @Test
    public void increaseBudgetIncreasesBudget() {

    }

    @Test
    public void increaseBudgetThrowsNegativeAmountException() {
        assertThrows(NegativeAmountException.class, () ->
                budgetService.increaseBudget(1, BigDecimal.valueOf(-100), "USD")
        );
    }



    @Test
    public void deleteBudgetByIdRemovesBudget() {

    }

    @Test
    public void getAllBudgetsReturnsAllBudgets() {

    }

    @Test
    public void decreaseBudgetThrowsNegativeAmountException() {
        assertThrows(NegativeAmountException.class, () ->
                budgetService.decreaseBudget(1, BigDecimal.valueOf(-100), "USD")
        );
    }


    @Test
    public void decreaseBudgetDecreasesBudget() {
        accountService.createAccountWithBudget("TEST",BigDecimal.valueOf(1000), "USD");

        budgetService.decreaseBudget(1, BigDecimal.valueOf(300), "USD");
        Budget updatedBudget = budgetDao.find(1);
        assertEquals(BigDecimal.valueOf(700), updatedBudget.getBalance());
    }

}

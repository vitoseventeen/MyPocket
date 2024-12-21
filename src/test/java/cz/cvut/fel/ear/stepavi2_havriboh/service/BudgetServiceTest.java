package cz.cvut.fel.ear.stepavi2_havriboh.service;


import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.BudgetDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.CategoryDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.BudgetNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.CategoryNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.EmptyCurrencyException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.BudgetService;
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
    private CategoryDao categoryDao;

    @Autowired
    private BudgetService budgetService;

    private Category testCategory;
    private Budget testBudget;

    @BeforeEach
    public void setUp() {
        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Description");
        testCategory.setDefaultLimit(BigDecimal.valueOf(1000));
        categoryDao.persist(testCategory);

        testBudget = new Budget();
        testBudget.setTargetAmount(BigDecimal.valueOf(1000));
        testBudget.setCurrentAmount(BigDecimal.valueOf(200));
        testBudget.setCurrency("USD");
        testBudget.setCategory(testCategory);
        budgetDao.persist(testBudget);
    }

    @Test
    void createBudgetForCategoryId() {
        Category category = new Category();
        category.setName("Category");
        category.setDescription("Description");
        category.setDefaultLimit(BigDecimal.TEN);
        categoryDao.persist(category);

        Budget budget = new Budget();
        budget.setTargetAmount(BigDecimal.valueOf(1000));
        budget.setCurrentAmount(BigDecimal.ZERO);
        budget.setCurrency("CZK");

        budgetService.createBudgetForCategoryById(category.getId(), budget.getTargetAmount(), budget.getCurrency());

        assertNotNull(budget);
        assertEquals(BigDecimal.valueOf(1000), budget.getTargetAmount());
        assertEquals("CZK", budget.getCurrency());
    }

    @Test
    public void createBudgetForCategoryByIdThrowsCategoryNotFoundException() {
        assertThrows(CategoryNotFoundException.class, () ->
                budgetService.createBudgetForCategoryById(999, new BigDecimal("500.00"), "USD")
        );
    }

    @Test
    public void increaseBudgetIncreasesBudget() {
        Category category = new Category();
        Budget budget = new Budget();
        budget.setTargetAmount(new BigDecimal("1000"));
        budget.setCurrentAmount(new BigDecimal("100"));
        budget.setCurrency("USD");
        budget.setCategory(category);
        budgetDao.persist(budget);

        budgetService.increaseBudget(budget.getId(), new BigDecimal("200"));
        Budget updatedBudget = budgetDao.find(budget.getId());
        assertEquals(new BigDecimal("300"), updatedBudget.getCurrentAmount());
    }

    @Test
    public void increaseBudgetThrowsNegativeAmountException() {
        Category category = new Category();
        Budget budget = new Budget();
        budget.setTargetAmount(new BigDecimal("1000"));
        budget.setCurrentAmount(new BigDecimal("100"));
        budget.setCurrency("USD");
        budget.setCategory(category);
        budgetDao.persist(budget);

        assertThrows(NegativeAmountException.class, () ->
                budgetService.increaseBudget(budget.getId(), new BigDecimal("-50"))
        );
    }

    @Test
    public void getTotalRemainingFundsTest() {
        Category category2 = new Category();
        category2.setName("Test Category 2");
        category2.setDescription("Another Description");
        category2.setDefaultLimit(BigDecimal.valueOf(1500));
        categoryDao.persist(category2);

        Budget budget2 = new Budget();
        budget2.setTargetAmount(new BigDecimal("500"));
        budget2.setCurrentAmount(new BigDecimal("300"));
        budget2.setCurrency("EUR");
        budget2.setCategory(category2);
        budgetDao.persist(budget2);

        BigDecimal totalRemainingFunds = budgetService.getTotalRemainingFunds();
        assertEquals(new BigDecimal("1000"), totalRemainingFunds);
    }

    @Test
    public void deleteBudgetByIdRemovesBudget() {
        Category category = new Category();
        Budget budget = new Budget();
        budget.setTargetAmount(BigDecimal.valueOf(1000));
        budget.setCurrentAmount(BigDecimal.valueOf(500));
        budget.setCurrency("USD");
        budget.setCategory(category);
        budgetDao.persist(budget);

        budgetService.deleteBudgetById(budget.getId());
        assertThrows(BudgetNotFoundException.class, () ->
                budgetService.getBudgetById(budget.getId())
        );
    }

    @Test
    public void getAllBudgetsReturnsAllBudgets() {
        Category category2 = new Category();
        category2.setName("Test Category 2");
        category2.setDescription("Another Description");
        category2.setDefaultLimit(BigDecimal.valueOf(1500));
        categoryDao.persist(category2);

        Budget budget2 = new Budget();
        budget2.setTargetAmount(BigDecimal.valueOf(500));
        budget2.setCurrentAmount(BigDecimal.valueOf(100));
        budget2.setCurrency("EUR");
        budget2.setCategory(category2);
        budgetDao.persist(budget2);

        List<Budget> budgets = budgetService.getAllBudgets();
        assertEquals(2, budgets.size());
        assertTrue(budgets.contains(testBudget));
        assertTrue(budgets.contains(budget2));
    }

    @Test
    public void createBudgetForCategoryByIdThrowsEmptyCurrencyException() {
        assertThrows(EmptyCurrencyException.class, () ->
                budgetService.createBudgetForCategoryById(testCategory.getId(), BigDecimal.valueOf(1000), null)
        );
    }


    @Test
    public void getRemainingLimitReturnsCorrectValue() {
        Category category = new Category();
        Budget budget = new Budget();
        budget.setTargetAmount(BigDecimal.valueOf(1000));
        budget.setCurrentAmount(BigDecimal.valueOf(300));
        budget.setCurrency("USD");
        budget.setCategory(category);
        budgetDao.persist(budget);

        BigDecimal remainingLimit = budgetService.getRemainingLimit(budget.getId());
        assertEquals(BigDecimal.valueOf(700), remainingLimit);
    }

    @Test
    public void getRemainingLimitThrowsBudgetNotFoundException() {
        assertThrows(BudgetNotFoundException.class, () ->
                budgetService.getRemainingLimit(999)
        );
    }

    @Test
    public void decreaseBudgetDecreasesCurrentAmount() {
        Category category = new Category();
        Budget budget = new Budget();
        budget.setTargetAmount(BigDecimal.valueOf(1000));
        budget.setCurrentAmount(BigDecimal.valueOf(500));
        budget.setCurrency("USD");
        budget.setCategory(category);
        budgetDao.persist(budget);

        budgetService.decreaseBudget(budget.getId(), BigDecimal.valueOf(200));
        Budget updatedBudget = budgetDao.find(budget.getId());

        assertEquals(BigDecimal.valueOf(300), updatedBudget.getCurrentAmount());
    }

    @Test
    public void decreaseBudgetThrowsNegativeAmountException() {
        Category category = new Category();
        Budget budget = new Budget();
        budget.setTargetAmount(BigDecimal.valueOf(1000));
        budget.setCurrentAmount(BigDecimal.valueOf(500));
        budget.setCurrency("USD");
        budget.setCategory(category);
        budgetDao.persist(budget);

        assertThrows(NegativeAmountException.class, () ->
                budgetService.decreaseBudget(budget.getId(), BigDecimal.valueOf(-50))
        );
    }

    @Test
    public void decreaseBudgetThrowsBudgetNotFoundException() {
        assertThrows(BudgetNotFoundException.class, () ->
                budgetService.decreaseBudget(999, BigDecimal.valueOf(100)) // Assuming this ID doesn't exist
        );
    }

}

package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.CategoryDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.TransactionDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.*;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Budget;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Category;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TransactionServiceTest {
    @Autowired
    private TransactionService transactionService;

    @SpyBean
    private TransactionDao transactionDao;

    @SpyBean
    private UserDao userDao;

    @SpyBean
    private CategoryDao categoryDao;

    private User user;
    private Category category;
    private Transaction transaction;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1);
        user.setUsername("Test User");

        category = new Category();
        category.setId(1);
        category.setName("Test Category");
        category.setDefaultLimit(BigDecimal.valueOf(100));


        transaction = new Transaction();
        transaction.setId(1);
        transaction.setAmount(BigDecimal.valueOf(50));
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setDate(LocalDate.now());
        transaction.setDescription("Test Transaction");
    }

    @Test
    public void createTransactionCreatesTransactionIfValid() {
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(category);
        when(transactionDao.getTotalSpentByCategory(category)).thenReturn(Optional.of(BigDecimal.valueOf(20)));

        transactionService.createTransaction(BigDecimal.valueOf(50), LocalDate.now(), "Valid Transaction", "EXPENSE", 1, 1);

        verify(transactionDao, times(1)).persist(any(Transaction.class));
    }

    @Test
    public void createTransactionThrowsUserNotFoundException() {
        when(userDao.find(1)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () ->
                transactionService.createTransaction(BigDecimal.valueOf(50), LocalDate.now(), "Invalid Transaction", "EXPENSE", 1, 1));
    }

    @Test
    public void createTransactionThrowsCategoryNotFoundException() {
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(null);

        assertThrows(CategoryNotFoundException.class, () ->
                transactionService.createTransaction(BigDecimal.valueOf(50), LocalDate.now(), "Invalid Transaction", "EXPENSE", 1, 1));
    }

    @Test
    public void createTransactionThrowsNegativeAmountException() {
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(category);

        assertThrows(NegativeAmountException.class, () ->
                transactionService.createTransaction(BigDecimal.valueOf(-10), LocalDate.now(), "Invalid Transaction", "EXPENSE", 1, 1));
    }

    @Test
    public void createTransactionWarnsIfExceedsCategoryLimit() {
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(category);

        when(transactionDao.getTotalSpentByCategory(category)).thenReturn(Optional.of(BigDecimal.valueOf(90)));

        Budget budget = new Budget();
        budget.setCurrentAmount(BigDecimal.valueOf(500));
        category.setBudget(budget);

        BigDecimal amount = BigDecimal.valueOf(20); // Total spent would be 90 + 20 = 110, exceeding limit of 100
        LocalDate date = LocalDate.now();
        String description = "Exceeding Transaction";
        String type = "EXPENSE";

        transactionService.createTransaction(amount, date, description, type, 1, 1);

        BigDecimal expectedRemainingAmount = BigDecimal.valueOf(600).subtract(BigDecimal.valueOf(20)); // Exceeded by 10
        assertEquals(expectedRemainingAmount, budget.getCurrentAmount(), "Budget remaining amount is incorrect");

        verify(transactionDao, times(1)).persist(any(Transaction.class));

     }

    @Test
    public void createRecurringTransactionCreatesMultipleTransactions() {
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(category);

        when(transactionDao.getTotalSpentByCategory(category)).thenReturn(Optional.of(BigDecimal.ZERO));

        BigDecimal amount = BigDecimal.valueOf(50);
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        String description = "Recurring Subscription";
        String type = "EXPENSE";
        int intervalDays = 7;
        LocalDate endDate = startDate.plusWeeks(3);

        transactionService.createRecurringTransaction(amount, startDate, description, type, 1, 1, intervalDays, endDate);

        int expectedOccurrences = 4;
        verify(transactionDao, times(expectedOccurrences)).persist(any(Transaction.class));
    }

    @Test
    public void createRecurringTransactionThrowsNegativeIntervalException() {
        assertThrows(NegativeIntervalException.class, () ->
                transactionService.createRecurringTransaction(
                        BigDecimal.valueOf(10), LocalDate.now(), "Invalid Recurring Transaction", "EXPENSE", 1, 1, -5, LocalDate.now().plusDays(21)));
    }

    @Test
    public void getTransactionsByUserIdReturnsTransactions() {
        when(userDao.find(1)).thenReturn(user);
        when(transactionDao.findTransactionsByUser(user)).thenReturn(Collections.singletonList(transaction));

        var transactions = transactionService.getTransactionsByUserId(1);

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
    }

    @Test
    public void getTotalSpentByUserIdCalculatesTotal() {
        when(userDao.find(1)).thenReturn(user);
        when(transactionDao.findTransactionsByUser(user)).thenReturn(Collections.singletonList(transaction));

        BigDecimal totalSpent = transactionService.getTotalSpentByUserId(1);

        assertEquals(BigDecimal.valueOf(50), totalSpent);
    }

    @Test
    public void deleteTransactionByIdDeletesTransaction() {
        User testUser = new User();
        testUser.setUsername("Test User");
        userDao.persist(testUser);

        Category testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Description");
        testCategory.setDefaultLimit(BigDecimal.valueOf(500));
        categoryDao.persist(testCategory);

        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setDate(LocalDate.now());
        testTransaction.setDescription("Test Transaction");
        testTransaction.setType("EXPENSE");
        testTransaction.setUser(testUser);
        testTransaction.setCategory(testCategory);
        transactionDao.persist(testTransaction);

        transactionService.deleteTransactionById(testTransaction.getId());
        Transaction deletedTransaction = transactionDao.find(testTransaction.getId());
        assertNull(deletedTransaction);
    }

    @Test
    public void deleteTransactionByIdThrowsTransactionNotFoundException() {
        when(transactionDao.findTransactionById(1)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.deleteTransactionById(1));
    }
}

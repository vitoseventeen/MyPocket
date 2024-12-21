package cz.cvut.fel.ear.stepavi2_havriboh.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.CategoryDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.TransactionDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.InvalidDateException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.InvalidTransactionTypeException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.TransactionNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.TransactionService;
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

import static cz.cvut.fel.ear.stepavi2_havriboh.main.model.TransactionIntervalType.MONTHS;
import static org.junit.jupiter.api.Assertions.*;
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

    @SpyBean
    private AccountDao accountDao;

    private User user;
    private Category category;
    private Account account;


    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("Test User");
        user.setPassword("Test Password");
        user.setEmail("asd@s.s");
        user.setRole(Role.USER);
        userDao.persist(user);

        category = new Category();
        category.setName("Test Category");
        category.setDefaultLimit(BigDecimal.valueOf(100));
        category.setDescription("Test Description");
        categoryDao.persist(category);


        account = new Account();
        account.setBalance(BigDecimal.valueOf(1000));
        account.setCurrency("CZK");
        account.setAccountName("Test Account");
        account.setTransactions(Collections.singletonList(new Transaction()));
        account.setUsers(Collections.singletonList(user));
        accountDao.persist(account);


    }

    @Test
    public void createTransactionWithValidDate() {
        // Arrange
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(category);
        when(accountDao.find(1)).thenReturn(account);
        when(transactionDao.getTotalSpentByCategory(category)).thenReturn(Optional.of(BigDecimal.valueOf(90)));

        Budget budget = new Budget();
        budget.setCurrentAmount(BigDecimal.valueOf(500));
        category.setBudget(budget);

        BigDecimal amount = BigDecimal.valueOf(20); // Total spent would be 90 + 20 = 110, exceeding limit of 100
        LocalDate date = LocalDate.of(2024, 12, 18);
        String description = "Valid Transaction";
        TransactionType type = TransactionType.EXPENSE;

        // Act
        transactionService.createTransaction(amount, date, description, type, 1, 1, 1);

        // Assert
        BigDecimal expectedRemainingAmount = BigDecimal.valueOf(600).subtract(BigDecimal.valueOf(20)); // Exceeded by 10
        assertEquals(expectedRemainingAmount, budget.getCurrentAmount(), "Budget remaining amount is incorrect");
        verify(transactionDao, times(1)).persist(any(Transaction.class));
    }

    @Test
    public void createTransactionWithInvalidDate_nullDate() {
        // Arrange
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(category);
        when(accountDao.find(1)).thenReturn(account);

        BigDecimal amount = BigDecimal.valueOf(20);
        LocalDate date = null; // Invalid date
        String description = "Invalid Transaction";
        TransactionType type = TransactionType.EXPENSE;

        // Act & Assert
        assertThrows(InvalidDateException.class, () ->
                transactionService.createTransaction(amount, date, description, type, 1, 1, 1));
    }


    @Test
    public void createRecurringTransactionWithValidDates() {
        // Arrange
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(category);
        when(accountDao.find(1)).thenReturn(account);
        when(transactionDao.getTotalSpentByCategory(category)).thenReturn(Optional.of(BigDecimal.valueOf(90)));

        Budget budget = new Budget();
        budget.setCurrentAmount(BigDecimal.valueOf(500));
        category.setBudget(budget);

        BigDecimal amount = BigDecimal.valueOf(20);
        LocalDate startDate = LocalDate.of(2024, 12, 18);
        String description = "Recurring Transaction";
        TransactionType type = TransactionType.EXPENSE;
        int interval = 1; // Monthly interval

        // Act
        transactionService.createRecurringTransaction(amount, startDate, description, type, 1, 1, 1, interval, MONTHS);

        // Assert: Check if the first transaction is created and the budget is updated
        BigDecimal expectedRemainingAmount = BigDecimal.valueOf(600).subtract(BigDecimal.valueOf(20)); // Exceeded by 10
        assertEquals(expectedRemainingAmount, budget.getCurrentAmount(), "Budget remaining amount is incorrect");
        verify(transactionDao, times(1)).persist(any(Transaction.class)); // Ensure at least 1 transaction was created
    }

    @Test
    public void createRecurringTransactionWithInvalidDate_nullDate() {
        // Arrange
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(category);
        when(accountDao.find(1)).thenReturn(account);

        BigDecimal amount = BigDecimal.valueOf(20);
        LocalDate startDate = null; // Invalid date
        String description = "Invalid Recurring Transaction";
        TransactionType type = TransactionType.EXPENSE;
        int interval = 1; // Monthly interval

        // Act & Assert
        assertThrows(InvalidDateException.class, () ->
                transactionService.createRecurringTransaction(amount, startDate, description, type, 1, 1, 1, interval, MONTHS));
    }

    @Test
    public void createTransactionThrowsInvalidInvalidTransactionTypeException() {
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(category);
        when(accountDao.find(1)).thenReturn(account);

        assertThrows(InvalidTransactionTypeException.class, () ->
                transactionService.createTransaction(BigDecimal.valueOf(50), LocalDate.now(), "Invalid Transaction", null, 1, 1, 1));
    }

    @Test
    public void createTransactionWarnsIfExceedsCategoryLimit() {
        when(userDao.find(1)).thenReturn(user);
        when(categoryDao.find(1)).thenReturn(category);
        when(accountDao.find(1)).thenReturn(account);
        when(transactionDao.getTotalSpentByCategory(category)).thenReturn(Optional.of(BigDecimal.valueOf(90)));

        Budget budget = new Budget();
        budget.setCurrentAmount(BigDecimal.valueOf(500));
        category.setBudget(budget);

        BigDecimal amount = BigDecimal.valueOf(20); // Total spent would be 90 + 20 = 110, exceeding limit of 100
        LocalDate date = LocalDate.now();
        String description = "Exceeding Transaction";
        TransactionType type = TransactionType.EXPENSE;

        transactionService.createTransaction(amount, date, description, type, 1,1, 1);

        BigDecimal expectedRemainingAmount = BigDecimal.valueOf(600).subtract(BigDecimal.valueOf(20)); // Exceeded by 10
        assertEquals(expectedRemainingAmount, budget.getCurrentAmount(), "Budget remaining amount is incorrect");

        verify(transactionDao, times(1)).persist(any(Transaction.class));

    }


    @Test
    public void deleteTransactionByIdDeletesTransaction() {
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setDate(LocalDate.now());
        testTransaction.setDescription("Test Transaction");
        testTransaction.setType(TransactionType.EXPENSE);
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

    @Test
    public void getTransactionByIdReturnsTransaction() {
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setDate(LocalDate.now());
        testTransaction.setDescription("Test Transaction");
        testTransaction.setType(TransactionType.EXPENSE);
        transactionDao.persist(testTransaction);

        Transaction returnedTransaction = transactionService.getTransactionById(testTransaction.getId());
        assertEquals(testTransaction, returnedTransaction);
    }

    @Test
    public void getTransactionByIdThrowsTransactionNotFoundException() {
        when(transactionDao.findTransactionById(1)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.getTransactionById(1));
    }

}

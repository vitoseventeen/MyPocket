package cz.cvut.fel.ear.stepavi2_havriboh.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.TransactionNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.TransactionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TransactionServiceTest {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private BudgetDao budgetDao;

    private Transaction testTransaction;
    private User testUser;
    private Account testAccount;
    private Budget testBudget;
    private Category testCategory;

    @BeforeEach
    public void setUp() {

        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        userDao.persist(testUser);

        testAccount = new Account();
        testAccount.setName("Test Account");
        testAccount.setCreator(testUser);

        accountDao.persist(testAccount);
        testBudget = new Budget();
        testBudget.setBalance(BigDecimal.valueOf(1000));
        testBudget.setCurrency(Currency.valueOf("CZK"));
        testBudget.setAccount(testAccount);
        testAccount.setBudget(testBudget);
        budgetDao.persist(testBudget);


        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Description");
        categoryDao.persist(testCategory);

        testTransaction = new Transaction();
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setBudget(testBudget);
        testTransaction.setDate(LocalDate.now());
        testTransaction.setDescription("Test Transaction");
        testTransaction.setType(TransactionType.EXPENSE);
        testTransaction.setCategory(testCategory);
        testTransaction.setAccount(testAccount);
        transactionDao.persist(testTransaction);

    }

    @Test
    public void createTransactionCreatingTransaction() {
        BigDecimal amount = BigDecimal.valueOf(200);
        Currency currency = Currency.CZK;
        LocalDate date = LocalDate.now();
        String description = "New Transaction";
        TransactionType type = TransactionType.EXPENSE;
        int accountId = testAccount.getId();
        int categoryId = testCategory.getId();

        transactionService.createTransaction(amount, currency, date, description, type, accountId, categoryId);

        Transaction createdTransaction = transactionDao.findAll().get(1);
        assertNotNull(createdTransaction);
        assertEquals(amount, createdTransaction.getAmount());
        assertEquals(description, createdTransaction.getDescription());
        assertEquals(type, createdTransaction.getType());
        assertEquals(testAccount, createdTransaction.getAccount());
        assertEquals(testCategory, createdTransaction.getCategory());

    }

    @Test
    public void createTransactionWithInvalidDataThrowsNegativeAmountException() {
        assertThrows(NegativeAmountException.class, () -> {
            transactionService.createTransaction(BigDecimal.valueOf(-100), Currency.USD, LocalDate.now(), "blabla", TransactionType.EXPENSE, testAccount.getId(), testCategory.getId());
        });
    }

    @Test
    public void updateTransactionUpdatesTransaction() {
        int transactionId = testTransaction.getId();
        BigDecimal newAmount = BigDecimal.valueOf(150);
        Currency newCurrency = Currency.CZK;
        LocalDate newDate = LocalDate.now();
        String newDescription = "Updated Transaction";
        TransactionType newType = TransactionType.EXPENSE;
        int accountId = testAccount.getId();
        int categoryId = testCategory.getId();

        transactionService.updateTransaction(transactionId, newAmount, newCurrency, newDate, newDescription, newType, accountId, categoryId);

        Transaction updatedTransaction = transactionDao.find(transactionId);
        assertNotNull(updatedTransaction);
        assertEquals(newAmount, updatedTransaction.getAmount());
        assertEquals(newDescription, updatedTransaction.getDescription());
        assertEquals(newType, updatedTransaction.getType());
        assertEquals(testAccount, updatedTransaction.getAccount());
        assertEquals(testCategory, updatedTransaction.getCategory());
    }

    @Test
    public void updateTransactionWithInvalidDataThrowsTransactionNotFoundException() {
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.updateTransaction(-1, BigDecimal.valueOf(1000), Currency.USD, LocalDate.now(), "blabla", TransactionType.EXPENSE, testAccount.getId(), testCategory.getId());
        });
    }

    @Test
    public void deleteTransactionByIdDeletesTransaction() {
        transactionService.deleteTransactionById(testTransaction.getId());
        Transaction deletedTransaction = transactionDao.find(testTransaction.getId());
        assertNull(deletedTransaction);
    }

    @Test
    public void deleteTransactionByIdThrowsTransactionNotFoundException() {
        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.deleteTransactionById(1111));
    }

    @Test
    public void getTransactionByIdReturnsTransaction() {
        Transaction returnedTransaction = transactionService.getTransactionById(testTransaction.getId());
        assertEquals(testTransaction, returnedTransaction);
    }

    @Test
    public void getTransactionByIdThrowsTransactionNotFoundException() {
        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.getTransactionById(99999999));
    }


}

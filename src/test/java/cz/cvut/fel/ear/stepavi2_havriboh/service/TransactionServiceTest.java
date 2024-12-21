package cz.cvut.fel.ear.stepavi2_havriboh.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.*;
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

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private BudgetDao budgetDao;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void createTransactionWithValidData() {

    }

    @Test
    public void createTransactionWithInvalidData() {

    }



    @Test
    public void deleteTransactionByIdDeletesTransaction() {
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setBudget(null);
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
        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.deleteTransactionById(1111));
    }

    @Test
    public void getTransactionByIdReturnsTransaction() {
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setDate(LocalDate.now());
        testTransaction.setBudget(null);
        testTransaction.setDescription("Test Transaction");
        testTransaction.setType(TransactionType.EXPENSE);
        transactionDao.persist(testTransaction);

        Transaction returnedTransaction = transactionService.getTransactionById(testTransaction.getId());
        assertEquals(testTransaction, returnedTransaction);
    }

    @Test
    public void getTransactionByIdThrowsTransactionNotFoundException() {
        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.getTransactionById(99999));
    }

}

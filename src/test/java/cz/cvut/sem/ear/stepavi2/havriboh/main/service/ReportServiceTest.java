package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.CategoryDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.ReportDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.TransactionDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.InvalidDateException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.NotPremiumUserException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.ReportNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Category;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Report;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private UserDao userDao;
    @Autowired
    private CategoryDao categoryDao;


    @Test
    void createReportThrowsExceptionForNonPremiumUser() {
        User testUser = new User();
        testUser.setEmail("nonpremium@example.com");
        testUser.setUsername("nonPremiumUser");
        if (testUser.isSubscribed()) {
            userService.cancelSubscription(testUser);
        }
        userDao.persist(testUser);

        assertThrows(NotPremiumUserException.class, () ->
                reportService.createReport(testUser.getId(), LocalDate.now().minusDays(5), LocalDate.now())
        );
    }

    @Test
    void createReportCreatesReportForPremiumUser() {
        // Создаем подписанного пользователя
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setSubscribed(true);
        userDao.persist(testUser);

        Category incomeCategory = new Category();
        incomeCategory.setName("Salary");
        categoryDao.persist(incomeCategory);

        Transaction incomeTransaction = new Transaction();
        incomeTransaction.setUser(testUser);
        incomeTransaction.setCategory(incomeCategory);
        incomeTransaction.setAmount(BigDecimal.valueOf(1000));
        incomeTransaction.setDate(LocalDate.now().minusDays(2));
        transactionDao.persist(incomeTransaction);

        reportService.createReport(testUser.getId(), LocalDate.now().minusDays(5), LocalDate.now());

        List<Report> reports = reportDao.findReportsByUserId(testUser.getId());
        assertEquals(1, reports.size());

        Report createdReport = reports.get(0);
        assertNotNull(createdReport);
        assertEquals(testUser.getId(), createdReport.getUser().getId());
        assertEquals(LocalDate.now().minusDays(5), createdReport.getFromDate());
        assertEquals(LocalDate.now(), createdReport.getToDate());
    }

    @Test
    void createReportThrowsExceptionForInvalidDateRange() {
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setSubscribed(true);
        userDao.persist(testUser);

        assertThrows(InvalidDateException.class, () ->
                reportService.createReport(testUser.getId(), LocalDate.now(), LocalDate.now().minusDays(1))
        );
    }

    @Test
    void createReportAddsTransactionsToReport() {
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setSubscribed(true);
        userDao.persist(testUser);

        Category incomeCategory = new Category();
        Category spendingCategory = new Category();
        incomeCategory.setName("Salary");
        spendingCategory.setName("Food");
        categoryDao.persist(incomeCategory);
        categoryDao.persist(spendingCategory);

        Transaction incomeTransaction = new Transaction();
        incomeTransaction.setUser(testUser);
        incomeTransaction.setCategory(incomeCategory);
        incomeTransaction.setAmount(BigDecimal.valueOf(1000));
        incomeTransaction.setDate(LocalDate.now().minusDays(2));
        transactionDao.persist(incomeTransaction);

        Transaction spendingTransaction = new Transaction();
        spendingTransaction.setUser(testUser);
        spendingTransaction.setCategory(spendingCategory);
        spendingTransaction.setAmount(BigDecimal.valueOf(-200));
        spendingTransaction.setDate(LocalDate.now().minusDays(1));
        transactionDao.persist(spendingTransaction);

        reportService.createReport(testUser.getId(), LocalDate.now().minusDays(5), LocalDate.now());

        List<Report> reports = reportDao.findReportsByUserId(testUser.getId());
        assertEquals(1, reports.size());

        Report createdReport = reports.get(0);
        assertNotNull(createdReport);
        assertEquals(testUser.getId(), createdReport.getUser().getId());

        Map<String, BigDecimal> expectedIncome = new HashMap<>();
        expectedIncome.put(incomeCategory.getName(), BigDecimal.valueOf(1000));

        Map<String, BigDecimal> expectedSpending = new HashMap<>();
        expectedSpending.put(spendingCategory.getName(), BigDecimal.valueOf(200));
    }

    @Test
    void generateSpendingReportThrowsExceptionForNonPremiumUser() {
        User testUser = new User();
        testUser.setEmail("nonpremium@example.com");
        testUser.setUsername("nonPremiumUser");
        if (testUser.isSubscribed()) {
            userService.cancelSubscription(testUser);
        }
        userDao.persist(testUser);

        assertThrows(NotPremiumUserException.class, () ->
                reportService.createReport(
                        testUser.getId(), LocalDate.now().minusDays(5), LocalDate.now()
                )
        );
    }

    @Test
    void getReportByIdReturnsCorrectReport() {
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setSubscribed(true);
        userDao.persist(testUser);

        Report report = new Report();
        report.setFromDate(LocalDate.now().minusDays(10));
        report.setToDate(LocalDate.now());
        report.setUser(testUser);
        reportDao.persist(report);

        Report foundReport = reportService.getReportById(report.getId());
        assertNotNull(foundReport);
        assertEquals(report.getId(), foundReport.getId());
        assertEquals(report.getFromDate(), foundReport.getFromDate());
        assertEquals(report.getToDate(), foundReport.getToDate());
        assertEquals(report.getUser().getId(), foundReport.getUser().getId());
    }

    @Test
    void getReportByIdThrowsExceptionForInvalidId() {
        assertThrows(ReportNotFoundException.class, () ->
                reportService.getReportById(999)
        );
    }

    @Test
    void deleteReportByIdRemovesReport() {
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setSubscribed(true);
        userDao.persist(testUser);

        Report report = new Report();
        report.setFromDate(LocalDate.now().minusDays(10));
        report.setToDate(LocalDate.now());
        report.setUser(testUser);
        reportDao.persist(report);

        reportService.deleteReportById(report.getId());

        assertThrows(ReportNotFoundException.class, () ->
                reportService.getReportById(report.getId())
        );
    }

    @Test
    void updateReportByIdUpdatesCorrectly() {
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setSubscribed(true);
        userDao.persist(testUser);

        Report report = new Report();
        report.setFromDate(LocalDate.now().minusDays(10));
        report.setToDate(LocalDate.now());
        report.setUser(testUser);
        reportDao.persist(report);

        reportService.updateReportById(report.getId(), LocalDate.now().minusDays(20), LocalDate.now(), "income");

        Report updatedReport = reportService.getReportById(report.getId());
        assertNotNull(updatedReport);
        assertEquals(LocalDate.now(), updatedReport.getToDate());
        assertEquals(LocalDate.now().minusDays(20), updatedReport.getFromDate());
    }
}

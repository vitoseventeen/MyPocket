package cz.cvut.fel.ear.stepavi2_havriboh.service;


import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.CategoryDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.ReportDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.TransactionDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.InvalidDateException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NotPremiumUserException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.ReportNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Transaction;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.ReportService;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    private User createUser(String email, String username) {
        User testUser = new User();
        testUser.setEmail(email);
        testUser.setUsername(username);
        testUser.setPassword("password");
        testUser.setRole(Role.USER);
        userDao.persist(testUser);
        return testUser;
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        categoryDao.persist(category);
        return category;
    }

    private Transaction createTransaction(User user, Category category, BigDecimal amount, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transactionDao.persist(transaction);
        return transaction;
    }

    @Test
    void createReportThrowsExceptionForNonPremiumUser() {
        User testUser = createUser("nonpremium@example.com", "nonPremiumUser");

        assertThrows(NotPremiumUserException.class, () ->
                reportService.createReport(testUser.getId(), LocalDate.now().minusDays(5), LocalDate.now())
        );
    }

    @Test
    void createReportCreatesReportForPremiumUser() {
        User testUser = createUser("test@example.com", "testUser");
        testUser.setSubscriptionEndDate(LocalDate.now().plusMonths(1));

        Category incomeCategory = createCategory("Salary");
        createTransaction(testUser, incomeCategory, BigDecimal.valueOf(1000), LocalDate.now().minusDays(2));

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
        User testUser = createUser("test@example.com", "testUser");
        testUser.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
        assertThrows(InvalidDateException.class, () ->
                reportService.createReport(testUser.getId(), LocalDate.now(), LocalDate.now().minusDays(1))
        );
    }

    @Test
    void createReportAddsTransactionsToReport() {
        User testUser = createUser("test@example.com", "testUser");
        testUser.setSubscriptionEndDate(LocalDate.now().plusMonths(1));

        Category incomeCategory = createCategory("Salary");
        Category spendingCategory = createCategory("Food");
        createTransaction(testUser, incomeCategory, BigDecimal.valueOf(1000), LocalDate.now().minusDays(2));
        createTransaction(testUser, spendingCategory, BigDecimal.valueOf(-200), LocalDate.now().minusDays(1));

        reportService.createReport(testUser.getId(), LocalDate.now().minusDays(5), LocalDate.now());

        List<Report> reports = reportDao.findReportsByUserId(testUser.getId());
        assertEquals(1, reports.size());

        Report createdReport = reports.get(0);
        assertNotNull(createdReport);
        assertEquals(testUser.getId(), createdReport.getUser().getId());
    }

    @Test
    void generateSpendingReportThrowsExceptionForNonPremiumUser() {
        User testUser = createUser("nonpremium@example.com", "nonPremiumUser");

        assertThrows(NotPremiumUserException.class, () ->
                reportService.createReport(testUser.getId(), LocalDate.now().minusDays(5), LocalDate.now())
        );
    }

    @Test
    void getReportByIdReturnsCorrectReport() {
        User testUser = createUser("test@example.com", "testUser");

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
        User testUser = createUser("test@example.com", "testUser");

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
        User testUser = createUser("test@example.com", "testUser");

        Report report = new Report();
        report.setFromDate(LocalDate.now().minusDays(10));
        report.setToDate(LocalDate.now());
        report.setUser(testUser);
        reportDao.persist(report);

        reportService.updateReportDateById(report.getId(), LocalDate.now().minusDays(20), LocalDate.now());

        Report updatedReport = reportService.getReportById(report.getId());
        assertNotNull(updatedReport);
        assertEquals(LocalDate.now(), updatedReport.getToDate());
        assertEquals(LocalDate.now().minusDays(20), updatedReport.getFromDate());
    }
}

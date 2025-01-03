package cz.cvut.fel.ear.stepavi2_havriboh.service;


import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.ReportDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.TransactionDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.InvalidDateException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.ReportNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.ReportService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountDao accountDao;


    private Account testAccount;
    private Report testReport;

    @BeforeEach
    public void setUp() {
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        userDao.persist(testUser);

        testAccount = new Account();
        testAccount.setName("Test Account");
        testAccount.setCreator(testUser);
        accountDao.persist(testAccount);

        testReport = new Report();
        testReport.setFromDate(LocalDate.now().minusDays(10));
        testReport.setToDate(LocalDate.now());
        testReport.setAccount(testAccount);
        reportDao.persist(testReport);
    }


    @Test
    void createReportWithValidDates() {
        LocalDate fromDate = LocalDate.now().minusDays(5);
        LocalDate toDate = LocalDate.now().minusDays(1);

        reportService.createReport(testAccount.getId(), fromDate, toDate);

        List<Report> reports = reportDao.findAll();
        assertEquals(2, reports.size());
        Report createdReport = reports.get(1);
        assertEquals(fromDate, createdReport.getFromDate());
        assertEquals(toDate, createdReport.getToDate());
        assertEquals(testAccount, createdReport.getAccount());
    }

    @Test
    void createReportWithInvalidDatesThrowsInvalidDateException() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().minusDays(1);

        assertThrows(InvalidDateException.class, () ->
                reportService.createReport(testAccount.getId(), fromDate, toDate)
        );
    }

    @Test
    void getReportByIdWithExistingReport() {
        Report fetchedReport = reportService.getReportById(testReport.getId());

        assertNotNull(fetchedReport);
        assertEquals(testReport.getId(), fetchedReport.getId());
        assertEquals(testReport.getFromDate(), fetchedReport.getFromDate());
        assertEquals(testReport.getToDate(), fetchedReport.getToDate());
        assertEquals(testAccount, fetchedReport.getAccount());
    }

    @Test
    void getReportByIdWithNonExistingReportThrowsReportNotFoundException() {
        assertThrows(ReportNotFoundException.class, () ->
                reportService.getReportById(9999)
        );
    }

    @Test
    void deleteReportByIdWithExistingReport() {
        reportService.deleteReportById(testReport.getId());

        List<Report> reports = reportDao.findAll();
        assertTrue(reports.isEmpty());
    }

    @Test
    void updateReportDatesWithValidDates() {
        LocalDate newFromDate = LocalDate.now().minusDays(20);
        LocalDate newToDate = LocalDate.now().minusDays(15);

        reportService.updateReportDateById(testReport.getId(), newFromDate, newToDate);

        Report updatedReport = reportDao.find(testReport.getId());
        assertEquals(newFromDate, updatedReport.getFromDate());
        assertEquals(newToDate, updatedReport.getToDate());
    }

    @Test
    void updateReportDatesWithInvalidDatesThrowsInvalidDateException() {
        LocalDate newFromDate = LocalDate.now();
        LocalDate newToDate = LocalDate.now().minusDays(1);

        assertThrows(InvalidDateException.class, () ->
                reportService.updateReportDateById(testReport.getId(), newFromDate, newToDate)
        );
    }

    @Test
    void getAllReportsReturnsReportsList() {
        List<Report> reports = reportService.getAllReports();

        assertNotNull(reports);
        assertEquals(1, reports.size());
        assertEquals(testReport.getId(), reports.get(0).getId());
    }

    @Test
    void createReportLinksTransactionsCorrectly() {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.EXPENSE);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setDate(LocalDate.now().minusDays(5));
        transaction.setAccount(testAccount);
        transactionDao.persist(transaction);

        LocalDate fromDate = LocalDate.now().minusDays(10);
        LocalDate toDate = LocalDate.now();
        reportService.createReport(testAccount.getId(), fromDate, toDate);

        Report createdReport = reportDao.findAll().get(1);
        assertNotNull(createdReport);
        assertEquals(testAccount, createdReport.getAccount());
    }
}

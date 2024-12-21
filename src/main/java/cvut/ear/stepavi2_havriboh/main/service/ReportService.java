package cvut.ear.stepavi2_havriboh.main.service;

import cvut.ear.stepavi2_havriboh.main.dao.ReportDao;
import cvut.ear.stepavi2_havriboh.main.dao.TransactionDao;
import cvut.ear.stepavi2_havriboh.main.exception.InvalidDateException;
import cvut.ear.stepavi2_havriboh.main.exception.NotPremiumUserException;
import cvut.ear.stepavi2_havriboh.main.exception.ReportNotFoundException;
import cvut.ear.stepavi2_havriboh.main.exception.UserNotFoundException;
import cvut.ear.stepavi2_havriboh.main.model.Report;
import cvut.ear.stepavi2_havriboh.main.model.Transaction;
import cvut.ear.stepavi2_havriboh.main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    private final ReportDao reportDao;
    private final UserService userService;
    private final TransactionDao transactionDao;

    @Autowired
    public ReportService(ReportDao reportDao, UserService userService, TransactionDao transactionDao) {
        this.reportDao = reportDao;
        this.userService = userService;
        this.transactionDao = transactionDao;
    }

    // pridat kontrolu
    @Transactional
    public void createReport(int userId, LocalDate fromDate, LocalDate toDate) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        if (!user.isSubscribed()) {
            throw new NotPremiumUserException("User is not subscribed");
        }
        if (fromDate.isAfter(toDate)) {
            throw new InvalidDateException("From date must be before to date");
        }

        List<Transaction> transactions = transactionDao.findTransactionsByUserAndDateRange(user, fromDate, toDate);

        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();

        for (Transaction transaction : transactions) {
            String categoryName = transaction.getCategory().getName();
            BigDecimal amount = transaction.getAmount();

            if (amount.compareTo(BigDecimal.ZERO) > 0) { // income
                incomeByCategory.put(categoryName,
                        incomeByCategory.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
            } else { // spending
                spendingByCategory.put(categoryName,
                        spendingByCategory.getOrDefault(categoryName, BigDecimal.ZERO).add(amount.abs()));
            }
        }

        // Create the report object
        Report report = new Report();
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        report.setUser(user);

        reportDao.persist(report);
    }

    // pridat kontrolu
    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        return reportDao.findAll();
    }

    @Transactional(readOnly = true)
    public Report getReportById(int reportId) {
        Report report = reportDao.find(reportId);
        if (report == null) {
            throw new ReportNotFoundException("Report not found with ID: " + reportId);
        }
        return report;
    }

    @Transactional
    public void deleteReportById(int reportId) {
        Report report = getReportById(reportId);
        reportDao.remove(report);
    }

    @Transactional
    public void updateReportDateById(int reportId, LocalDate fromDate, LocalDate toDate ) {
        Report report = getReportById(reportId);
        if (fromDate.isAfter(toDate)) {
            throw new InvalidDateException("From date must be before to date");
        }
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        reportDao.update(report);
    }
}

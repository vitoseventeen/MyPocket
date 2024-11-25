package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.ReportDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.TransactionDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.*;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Report;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        StringBuilder reportContent = new StringBuilder();
        reportContent.append("Financial Report (").append(")\n");
        reportContent.append("Period: ").append(fromDate).append(" - ").append(toDate).append("\n\n");

        reportContent.append("Income by Category:\n");
        incomeByCategory.forEach((category, total) ->
                reportContent.append("  Category: ").append(category)
                        .append(", Total Income: ").append(total).append("\n"));

        reportContent.append("\nSpending by Category:\n");
        spendingByCategory.forEach((category, total) ->
                reportContent.append("  Category: ").append(category)
                        .append(", Total Spending: ").append(total).append("\n"));

        Report report = new Report();
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        report.setUser(user);

        reportDao.persist(report);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByUserId(int userId) {
        return reportDao.findReportsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByUserIdAndDateRange(int userId, LocalDate fromDate, LocalDate toDate) {
        return reportDao.findReportsByUserIdAndDateRange(userId, fromDate, toDate);
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

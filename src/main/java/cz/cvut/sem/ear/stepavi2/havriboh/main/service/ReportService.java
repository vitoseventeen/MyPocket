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
    public void generateSpendingReportByUserIdInDates(int id, LocalDate fromDate, LocalDate toDate) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        if (fromDate.isAfter(toDate)) {
            throw new InvalidDateException("From date must be before to date");
        }
        if (!user.isSubscribed()) {
            throw new NotPremiumUserException("User is not subscribed");
        }
        List<Transaction> transactions = transactionDao.findTransactionsByUserAndDateRange(user, fromDate, toDate);
        Map<String, BigDecimal> categorySpending = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {  // it is a spending transaction
                String category = transaction.getCategory().getName();
                categorySpending.put(category, categorySpending.getOrDefault(category, BigDecimal.ZERO).add(transaction.getAmount().abs()));
            }
        }


        Report report = new Report();
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        report.setReportType("spending");
        report.setUser(user);

        StringBuilder reportContent = new StringBuilder("Spending Report\n");
        categorySpending.forEach((category, total) -> {
            reportContent.append("Category: ").append(category).append(", Total Spending: ").append(total).append("\n");
        });

        reportDao.persist(report);
    }

    @Transactional
    public void createReport(LocalDate fromDate, LocalDate toDate, String reportType, int userId) {
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
        if (reportType.isEmpty()) {
            throw new EmptyReportTypeException("Report type cannot be empty");
        }
        Report report = new Report();
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        report.setReportType(reportType);
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
    public List<Report> getReportsByUserIdAndReportType(int userId, String reportType) {
        return reportDao.findReportsByUserIdAndReportType(userId, reportType);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByUserIdAndDateRangeAndReportType(int userId, LocalDate fromDate, LocalDate toDate, String reportType) {
        return reportDao.findReportsByUserIdAndDateRangeAndReportType(userId, fromDate, toDate, reportType);
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
    public void updateReportById(int reportId, LocalDate fromDate, LocalDate toDate, String reportType) {
        Report report = getReportById(reportId);
        if (fromDate.isAfter(toDate)) {
            throw new InvalidDateException("From date must be before to date");
        }
        if (reportType.isEmpty()) {
            throw new EmptyReportTypeException("Report type cannot be empty");
        }
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        report.setReportType(reportType);
        reportDao.update(report);
    }
}

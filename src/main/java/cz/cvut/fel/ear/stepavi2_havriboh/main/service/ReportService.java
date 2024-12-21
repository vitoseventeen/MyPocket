package cz.cvut.fel.ear.stepavi2_havriboh.main.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.ReportDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.TransactionDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.InvalidDateException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.ReportNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    @Autowired
    public ReportService(ReportDao reportDao, AccountDao userService, TransactionDao transactionDao) {
        this.reportDao = reportDao;
        this.accountDao = userService;
        this.transactionDao = transactionDao;
    }

    @Transactional
    public void createReport(int accountId, LocalDate fromDate, LocalDate toDate) {
        Account account = accountDao.find(accountId);
        if (account == null) {
            throw new ReportNotFoundException("Account not found with ID: " + accountId);
        }
        if (fromDate.isAfter(toDate)) {
            throw new InvalidDateException("From date must be before to date");
        }

        Report report = new Report();
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        report.setAccount(account);
        reportDao.persist(report);
    }

    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        List<Report> reports = reportDao.findAll();
        for (Report report : reports) {
            enrichReportWithTransactions(report);
        }
        return reports;
    }

    @Transactional(readOnly = true)
    public Report getReportById(int reportId) {
        Report report = reportDao.find(reportId);
        if (report == null) {
            throw new ReportNotFoundException("Report not found with ID: " + reportId);
        }
        enrichReportWithTransactions(report);
        return report;
    }

    @Transactional
    public void deleteReportById(int reportId) {
        Report report = getReportById(reportId);
        reportDao.remove(report);
    }

    @Transactional
    public void updateReportDateById(int reportId, LocalDate fromDate, LocalDate toDate) {
        Report report = getReportById(reportId);
        if (fromDate.isAfter(toDate)) {
            throw new InvalidDateException("From date must be before to date");
        }
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        reportDao.update(report);
    }

    private void enrichReportWithTransactions(Report report) {
        List<Transaction> transactions = transactionDao.findTransactionsByAccountAndDateRange(
                report.getAccount(), report.getFromDate(), report.getToDate());

        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();

        for (Transaction transaction : transactions) {
            String categoryName = transaction.getCategory().getName();
            BigDecimal amount = transaction.getAmount();

            if (transaction.isIncome()) {
                incomeByCategory.put(categoryName, incomeByCategory.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
            } else {
                spendingByCategory.put(categoryName, spendingByCategory.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                }
        }
        report.setIncomeByCategory(incomeByCategory);
        report.setSpendingByCategory(spendingByCategory);
    }
}
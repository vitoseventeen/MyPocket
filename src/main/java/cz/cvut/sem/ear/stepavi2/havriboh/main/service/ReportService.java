package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.ReportDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.TransactionDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Report;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    private User user;
    private TransactionDao transactionDao;

    private final ReportDao reportDao;

    @Autowired
    public ReportService(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public Report generateReport(User user, Date fromDate, Date toDate, String categoryType) {
        List<Transaction> transactions = transactionDao.findTransactionsByUserWithDatesAndCategory(user, fromDate, toDate, categoryType);

        /*
        BigDecimal income = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenses = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

         */

        //make report
        Report report = new Report();
        report.setReportType("Category report");
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        // add user to report, incomes, expenses

        return report;
    }

}

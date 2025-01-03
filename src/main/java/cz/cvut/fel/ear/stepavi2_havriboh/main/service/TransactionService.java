package cz.cvut.fel.ear.stepavi2_havriboh.main.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.CategoryDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.TransactionDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.utils.CurrencyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionDao transactionDao;
    private final CategoryDao categoryDao;
    private final AccountDao accountDao;

    @Autowired
    public TransactionService(TransactionDao transactionDao, CategoryDao categoryDao, AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.categoryDao = categoryDao;
        this.accountDao = accountDao;
    }

    protected boolean isValidData(BigDecimal amount, LocalDate date, String description, TransactionType type, int accountId, int categoryId) {
        Account account = accountDao.find(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with ID: " + accountId);
        }
        if (categoryDao.find(categoryId) == null) {
            throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
        }
        if (type == null) {
            throw new InvalidTransactionTypeException("Transaction type cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmountException("Amount must be a positive number");
        }
        if (date == null) {
            throw new InvalidDateException("Date cannot be null");
        }
        if (description == null || description.isEmpty()) {
            throw new EmptyDescriptionException("Description cannot be null or empty");
        }
        if (account.getBudget() == null) {
            throw new BudgetNotFoundException("Budget not found for account with ID: " + accountId);
        }
        return true;
    }



    @Transactional
    public void createTransaction(BigDecimal amount, Currency currency, LocalDate date, String description,
                                  TransactionType type, int accountId, int categoryId) {
        Category category = categoryDao.find(categoryId);
        Account account = accountDao.find(accountId);
        Budget budget = account.getBudget();

        if (!isValidData(amount, date, description, type, accountId, categoryId)) {
            throw new InvalidDataException("Invalid data");
        }

        if (!budget.getCurrency().equals(currency)) {
            amount = CurrencyConverter.convert(amount, currency.toString(), budget.getCurrency().toString());
        }
       switch (type) {
            case INCOME -> {
                budget.addBalance(amount);
            }
            case EXPENSE -> {
                if (budget.getBalance().compareTo(amount) < 0) {
                    throw new NegativeBalanceException("Insufficient funds in account with ID: " + accountId);
                }
                budget.subtractBalance(amount);
            }
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setAccount(account);

        transactionDao.persist(transaction);
    }

    @Transactional
    public void updateTransaction(int transactionId, BigDecimal amount, Currency currency, LocalDate date, String description,
                                  TransactionType type, int accountId, int categoryId) {
        Transaction transaction = transactionDao.find(transactionId);
        if (transaction == null) {
            throw new TransactionNotFoundException("Transaction not found with ID: " + transactionId);
        }

        Category category = categoryDao.find(categoryId);
        Account account = accountDao.find(accountId);
        Budget budget = account.getBudget();

        if (!isValidData(amount, date, description, type, accountId, categoryId)) {
            throw new InvalidDataException("Invalid data");
        }
        if (!budget.getCurrency().equals(currency)) {
            amount = CurrencyConverter.convert(amount, currency.toString(), budget.getCurrency().toString());
        }

        if (transaction.getType() == TransactionType.INCOME) {
            budget.addBalance(transaction.getAmount().negate());
        } else {
            budget.addBalance(transaction.getAmount());
        }

        if (type == TransactionType.INCOME) {
            budget.addBalance(amount);
        } else {
            if (budget.getBalance().compareTo(amount) < 0) {
                throw new NegativeBalanceException("Insufficient funds in account with ID: " + accountId);
            }
            budget.subtractBalance(amount);
        }

        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setAccount(account);
    }

    @Transactional
    public LocalDate calculateNextDate(LocalDate currentDate, int interval, TransactionIntervalType intervalUnit) {
        return switch (intervalUnit) {
            case DAYS -> currentDate.plusDays(interval);
            case WEEKS -> currentDate.plusWeeks(interval);
            case MONTHS -> currentDate.plusMonths(interval);
            case YEARS -> currentDate.plusYears(interval);
            default -> throw new InvalidDataException("Invalid interval unit: " + intervalUnit);
        };
    }

    @Transactional
    public void createRecurringTransaction(BigDecimal amount, Currency currency, LocalDate date, String description,
                                           TransactionType type, int accountId, int categoryId, int interval, TransactionIntervalType intervalUnit) {

        if (interval <= 0) {
            throw new NegativeIntervalException("The interval 'days' must be a positive number.");
        }

        createTransaction(amount, currency, date, description, type,accountId, categoryId);

        LocalDate nextDate = calculateNextDate(date, interval, intervalUnit);

        for (int i = 0; i < interval; i++) {
            createTransaction(amount, currency, nextDate, description, type, accountId, categoryId);
            nextDate = calculateNextDate(nextDate, 1, intervalUnit);
        }
    }

    @Transactional
    public void deleteTransactionById(int transactionId) {
        Transaction transaction = transactionDao.find(transactionId);
        if (transaction != null) {
            transactionDao.remove(transaction);
        } else {
            throw new TransactionNotFoundException("Transaction with ID " + transactionId + " not found");
        }
    }

    @Transactional(readOnly = true)
    public Transaction getTransactionById(int id) {
        Transaction transaction = transactionDao.find(id);
        if (transaction == null) {
            throw new TransactionNotFoundException("Transaction with ID " + id + " not found");
        }
        return transaction;
    }
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionDao.findAll();
    }
}

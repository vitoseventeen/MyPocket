package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.AccountDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.CategoryDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.TransactionDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.*;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionDao transactionDao;
    private final UserDao userDao;
    private final CategoryDao categoryDao;
    private final AccountDao accountDao;

    @Autowired
    public TransactionService(TransactionDao transactionDao, UserDao userDao, CategoryDao categoryDao, AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.userDao = userDao;
        this.categoryDao = categoryDao;
        this.accountDao = accountDao;
    }

    private boolean isValidData(BigDecimal amount, LocalDate date, String description, TransactionType type, int userId, int accountId, int categoryId) {
        if (userDao.find(userId) == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        if (categoryDao.find(categoryId) == null) {
            throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
        }
        if (type == null) {
            throw new InvalidTransactionTypeException("Transaction type cannot be null");
        }
        if (accountDao.find(accountId) == null) {
            throw new AccountNotFoundException("Account not found with ID: " + accountId);
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
        if (categoryDao.find(categoryId).getBudget() == null) {
            throw new BudgetNotFoundException("Category budget is null");
        }
        return true;
    }


    @Transactional
    public void createTransaction(BigDecimal amount, LocalDate date, String description,
                                  TransactionType type, int userId, int accountId, int categoryId) {
        User user = userDao.find(userId);
        Category category = categoryDao.find(categoryId);
        Account account = accountDao.find(accountId);
        Budget budget = category.getBudget();

        if (!isValidData(amount, date, description, type, userId, accountId, categoryId)) {
            throw new InvalidDataException("Invalid data");
        }

        if (type == TransactionType.EXPENSE) {
            account.decreaseBalance(amount);
        }
        if (type == TransactionType.INCOME) {
            account.increaseBalance(amount);
        }

        BigDecimal totalSpent = transactionDao.getTotalSpentByCategory(category).orElse(BigDecimal.ZERO);
        BigDecimal newTotal = totalSpent.add(amount);

        if (newTotal.compareTo(category.getDefaultLimit()) > 0) {
            try {
                throw new CategoryLimitExceededException("Transaction exceeds category limit! Category limit is exceeded by " +
                        newTotal.subtract(category.getDefaultLimit()) + ". Category budget is now negative.");
            } catch (CategoryLimitExceededException e) {
                e.printStackTrace();
            }

            budget.decreaseBudget(amount.subtract(category.getDefaultLimit()));
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setType(type);
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAccount(account);

        transactionDao.persist(transaction);

    }

    @Transactional
    public void updateTransaction(int transactionId, BigDecimal amount, LocalDate date, String description,
                                  TransactionType type) {

        Optional<Transaction> transaction = transactionDao.findTransactionById(transactionId);
        if (!transaction.isPresent()) {
            throw new TransactionNotFoundException("Transaction not found with ID: " + transactionId);
        }
        Transaction t = transaction.get();

        if (!isValidData(amount, date, description, type, t.getUser().getId(), t.getAccount().getId(), t.getCategory().getId())) {
            throw new InvalidDataException("Invalid data");
        }
        t.setAmount(amount);
        t.setDate(date);
        t.setDescription(description);
        t.setType(type);
        transactionDao.update(t);
    }

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
    public void createRecurringTransaction(BigDecimal amount, LocalDate date, String description,
                                           TransactionType type, int userId, int categoryId, int accountId, int interval, TransactionIntervalType intervalUnit) {

        if (interval <= 0) {
            throw new NegativeIntervalException("The interval 'days' must be a positive number.");
        }

        createTransaction(amount, date, description, type, userId, accountId, categoryId);

        LocalDate nextDate = calculateNextDate(date, interval, intervalUnit);

        for (int i = 1; i < interval; i++) {
            createTransaction(amount, nextDate, description, type, userId, accountId, categoryId);
            nextDate = calculateNextDate(nextDate, 1, intervalUnit);
        }
    }


    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByUserId(int userId) {
        User user = userDao.find(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        return transactionDao.findTransactionsByUser(user);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalSpentByUserId(int userId) {
        User user = userDao.find(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        List<Transaction> transactions = transactionDao.findTransactionsByUser(user);
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCategoryId(int categoryId) {
        Category category = categoryDao.find(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
        }

        return transactionDao.findTransactionsByCategory(category);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalSpentByCategoryId(int categoryId) {
        Category category = categoryDao.find(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
        }

        return transactionDao.getTotalSpentByCategory(category).orElse(BigDecimal.ZERO);
    }

    @Transactional
    public void deleteTransactionById(int transactionId) {
        Optional<Transaction> transaction = transactionDao.findTransactionById(transactionId);
        if (transaction.isPresent()) {
            transactionDao.remove(transaction.get());
        } else {
            throw new TransactionNotFoundException("Transaction with ID " + transactionId + " not found");
        }
    }

}

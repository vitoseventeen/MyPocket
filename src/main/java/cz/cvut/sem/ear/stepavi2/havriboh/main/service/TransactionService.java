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

    @Transactional
    public void createTransaction(BigDecimal amount, LocalDate date, String description,
                                  TransactionType type, int userId, int accountId, int categoryId) {
        User user = userDao.find(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        Category category = categoryDao.find(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
        }

        Account account = accountDao.find(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with ID: " + accountId);
        } else {
            if (type == TransactionType.EXPENSE) {
                account.decreaseBalance(amount);
            }
            if (type == TransactionType.INCOME) {
                account.increaseBalance(amount);
            }
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmountException("Amount must be a positive number");
        }

        BigDecimal totalSpent = transactionDao.getTotalSpentByCategory(category).orElse(BigDecimal.ZERO);
        BigDecimal newTotal = totalSpent.add(amount);

        if (newTotal.compareTo(category.getDefaultLimit()) > 0) {
            System.out.println("Warning: Transaction exceeds category limit! Category limit is exceeded by " +
                    newTotal.subtract(category.getDefaultLimit()) + ". Category budget is now negative.");

            Budget budget = category.getBudget();
            if (budget == null) {
                throw new BudgetNotFoundException("Category budget is null");
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
    public void createRecurringTransaction(BigDecimal amount, LocalDate date, String description, TransactionType type, int userId, int categoryId, int accountId, int days, LocalDate endDate) {
        if (days <= 0) {
            throw new NegativeIntervalException("The interval 'days' must be a positive number.");
        }

        // If endDate is null, set it to 1 year from the start date
        if (endDate == null) {
            endDate = date.plusYears(1);
        }

        for (LocalDate currentDate = date; !currentDate.isAfter(endDate); currentDate = currentDate.plusDays(days)) {
            createTransaction(amount, currentDate, description, type, userId, categoryId, accountId);
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

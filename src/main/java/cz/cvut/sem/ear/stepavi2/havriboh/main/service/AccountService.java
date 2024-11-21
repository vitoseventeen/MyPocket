package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.AccountDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.*;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountDao accountDao;
    private final UserDao userDao;

    @Autowired
    public AccountService(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @Transactional
    public void createAccount(String name, BigDecimal balance, String currency) {

        Account account = new Account();
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeBalanceException("Balance cannot be negative");
        }
        if (name.isBlank()) {
            throw new EmptyNameException("Name cannot be empty");
        }
        if (currency.isBlank()) {
            throw new EmptyCurrencyException("Currency cannot be empty");
        }
        account.setAccountName(name);
        account.setCurrency(currency);
        account.setBalance(balance);
        accountDao.persist(account);
    }

    @Transactional
    public void addUserToAccountById(int userId, int accountId) {
        Account account = accountDao.find(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }
        User user = userDao.find(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (!account.getUsers().contains(user)) {
            account.getUsers().add(user);
            accountDao.update(account);
        }
    }

    @Transactional
    public void removeUserFromAccountById(int userId, int accountId) {
        Account account = accountDao.find(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }
        User user = userDao.find(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (account.getUsers().contains(user)) {
            account.getUsers().remove(user);
            if (account.getUsers().isEmpty()) {
                throw new LastUserInAccountException("Cannot remove the last user from the account");
            }
            accountDao.update(account);
        }
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountDao.findAll();
    }

    @Transactional(readOnly = true)
    public Account getAccountById(int id) {
        Account account = accountDao.find(id);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }
        return account;
    }

    @Transactional
    public void deleteAccountById(int id) {
        Account account = accountDao.find(id);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }
        accountDao.remove(account);
    }


}

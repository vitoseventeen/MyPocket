package cz.cvut.fel.ear.stepavi2_havriboh.main.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Currency;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
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

    //TODO: Implement the following methods
    @Transactional
    public void createAccount() {
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
        else {
            throw new UserAlreadyInAccountException("User is already in the account");
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

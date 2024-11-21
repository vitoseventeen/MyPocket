package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.AccountDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
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
        account.setAccountName(name);
        account.setCurrency(currency);
        account.setBalance(balance);
        accountDao.persist(account);
    }

    @Transactional
    public void addUserToAccountById(int userId, int accountId) {
        Account account = accountDao.find(accountId);
        User user = userDao.find(userId);
        account.getUsers().add(user);
        accountDao.update(account);
    }

    @Transactional
    public void removeUserFromAccountById(int userId, int accountId) {
        Account account = accountDao.find(accountId);
        User user = userDao.find(userId);
        account.getUsers().remove(user);
        accountDao.update(account);
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountDao.findAll();
    }

    @Transactional(readOnly = true)
    public Account getAccountById(int id) {
        return accountDao.find(id);
    }

    @Transactional
    public void updateAccount(int id) {
        accountDao.update(accountDao.find(id));
    }

    @Transactional
    public void deleteAccountById(int id) {
        accountDao.remove(accountDao.find(id));
    }


}

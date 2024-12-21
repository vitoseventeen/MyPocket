package cz.cvut.fel.ear.stepavi2_havriboh.service;



import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.BudgetDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.AccountService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountDao accountDao;


    @Autowired
    private AccountService accountService;

    private User user;
    private Account account;
    @Autowired
    private BudgetDao budgetDao;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setEmail("ysers@gmail.com");
        user.setRole(Role.USER);

        userDao.persist(user);

        account = new Account();
        account.setName("account");
        account.setBudget(null);

        accountDao.persist(account);
    }


    @Test
    public void createAccountThrowsExceptionIfNameIsEmpty() {
        String accountName = "";
        BigDecimal balance = new BigDecimal(1000);
        String currency = "CZK";

        assertThrows(EmptyNameException.class, () ->
                accountService.createAccountWithBudget(accountName, balance, currency));
    }


    @Test
    public void addUserToAccountByIdAddsUserIfNotAlreadyAdded() {
        accountService.addUserToAccountById(user.getId(), account.getId());

        List<User> users = account.getUsers();
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
    }

    @Test
    public void addUserToAccountByIdThrowsExceptionIfAccountNotFound() {
        int invalidAccountId = 666;
        assertThrows(AccountNotFoundException.class, () ->
                accountService.addUserToAccountById(user.getId(), invalidAccountId));
    }

    @Test
    public void addUserToAccountByIdThrowsExceptionIfUserNotFound() {
        int invalidUserId = 666;
        assertThrows(UserNotFoundException.class, () ->
                accountService.addUserToAccountById(invalidUserId, account.getId()));
    }

    @Test
    public void addUserByIdDoesntAddUserIfAlreadyAdded() {
        accountService.addUserToAccountById(user.getId(), account.getId());
        assertThrows(UserAlreadyInAccountException.class, () ->
                accountService.addUserToAccountById(user.getId(), account.getId()));
    }

    @Test
    public void removeUserFromAccountByIdRemovesUserFromAccount() {
        accountService.addUserToAccountById(user.getId(), account.getId());
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        anotherUser.setPassword("password");
        anotherUser.setEmail("asdasd@gmail.comm");
        userDao.persist(anotherUser);
        accountService.addUserToAccountById(anotherUser.getId(), account.getId());
        accountService.removeUserFromAccountById(user.getId(), account.getId());

        List<User> users = account.getUsers();
        assertEquals(1, users.size());
    }

    @Test
    public void removeUserFromAccountByIdThrowsExceptionIfAccountNotFound() {
        int invalidAccountId = 999;
        assertThrows(AccountNotFoundException.class, () ->
                accountService.removeUserFromAccountById(user.getId(), invalidAccountId));
    }

    @Test
    public void removeUserFromAccountByIdThrowsExceptionIfUserNotFound() {
        int invalidUserId = 999;
        assertThrows(UserNotFoundException.class, () ->
                accountService.removeUserFromAccountById(invalidUserId, account.getId()));
    }

    @Test
    public void removeUserFromAccountByIdThrowsExceptionIfLastUser() {
        accountService.addUserToAccountById(user.getId(), account.getId());

        assertThrows(LastUserInAccountException.class, () ->
                accountService.removeUserFromAccountById(user.getId(), account.getId()));
    }

    @Test
    public void getAllAccountsReturnsAllAccounts() {
        Account anotherAccount = new Account();
        anotherAccount.setName("anotherAccount");
        anotherAccount.setBudget(null);
        accountDao.persist(anotherAccount);

        List<Account> accounts = accountService.getAllAccounts();

        assertEquals(2, accounts.size());
    }

    @Test
    public void getAccountByIdReturnsCorrectAccount() {
        Account foundAccount = accountService.getAccountById(account.getId());

        assertEquals(account.getId(), foundAccount.getId());
        assertEquals(account.getName(), foundAccount.getName());
    }

    @Test
    public void getAccountByIdThrowsExceptionIfAccountNotFound() {
        int invalidAccountId = 999;
        assertThrows(AccountNotFoundException.class, () ->
                accountService.getAccountById(invalidAccountId));
    }

    @Test
    public void deleteAccountByIdDeletesAccount() {
        Account newAccount = new Account();
        newAccount.setName("AccountToDelete");
        newAccount.setBudget(null);
        accountDao.persist(newAccount);

        accountService.deleteAccountById(newAccount.getId());

        Account deletedAccount = accountDao.find(newAccount.getId());
        assertNull(deletedAccount);
    }

    @Test
    public void deleteAccountByIdThrowsExceptionIfAccountNotFound() {
        int invalidAccountId = 999;
        assertThrows(AccountNotFoundException.class, () ->
                accountService.deleteAccountById(invalidAccountId));
    }
}

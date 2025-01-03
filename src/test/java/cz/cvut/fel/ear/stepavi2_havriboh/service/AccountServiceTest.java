package cz.cvut.fel.ear.stepavi2_havriboh.service;


import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.BudgetDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.AccountService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    @Autowired
    private BudgetDao budgetDao;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        userDao.persist(testUser);

        testAccount = new Account();
        testAccount.setName("Test Account");
        testAccount.setCreator(testUser);
        accountDao.persist(testAccount);
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
        accountService.addUserToAccountById(testUser.getId(), testAccount.getId());

        List<User> users = testAccount.getUsers();
        assertEquals(1, users.size());
        assertEquals(testUser.getId(), users.get(0).getId());
    }

    @Test
    public void addUserToAccountByIdThrowsExceptionIfAccountNotFound() {
        int invalidAccountId = 666;
        assertThrows(AccountNotFoundException.class, () ->
                accountService.addUserToAccountById(testUser.getId(), invalidAccountId));
    }

    @Test
    public void addUserToAccountByIdThrowsExceptionIfUserNotFound() {
        int invalidUserId = 666;
        assertThrows(UserNotFoundException.class, () ->
                accountService.addUserToAccountById(invalidUserId, testAccount.getId()));
    }

    @Test
    public void addUserByIdDoesntAddUserIfAlreadyAdded() {
        accountService.addUserToAccountById(testUser.getId(), testAccount.getId());
        assertThrows(UserAlreadyInAccountException.class, () ->
                accountService.addUserToAccountById(testUser.getId(), testAccount.getId()));
    }

    @Test
    public void removeUserFromAccountByIdRemovesUserFromAccount() {
        accountService.addUserToAccountById(testUser.getId(), testAccount.getId());
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        anotherUser.setPassword("password");
        anotherUser.setEmail("asdasd@gmail.comm");
        userDao.persist(anotherUser);
        accountService.addUserToAccountById(anotherUser.getId(), testAccount.getId());
        accountService.removeUserFromAccountById(testUser.getId(), testAccount.getId());

        List<User> users = testAccount.getUsers();
        assertEquals(1, users.size());
    }

    @Test
    public void removeUserFromAccountByIdThrowsExceptionIfAccountNotFound() {
        int invalidAccountId = 999;
        assertThrows(AccountNotFoundException.class, () ->
                accountService.removeUserFromAccountById(testUser.getId(), invalidAccountId));
    }

    @Test
    public void removeUserFromAccountByIdThrowsExceptionIfUserNotFound() {
        int invalidUserId = 999;
        assertThrows(UserNotFoundException.class, () ->
                accountService.removeUserFromAccountById(invalidUserId, testAccount.getId()));
    }

    @Test
    public void removeUserFromAccountByIdThrowsExceptionIfLastUser() {
        accountService.addUserToAccountById(testUser.getId(), testAccount.getId());

        assertThrows(LastUserInAccountException.class, () ->
                accountService.removeUserFromAccountById(testUser.getId(), testAccount.getId()));
    }

    @Test
    public void getAllAccountsReturnsAllAccounts() {
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        anotherUser.setPassword("password");
        anotherUser.setEmail("parek@gmail.com");
        userDao.persist(anotherUser);

        Account anotherAccount = new Account();
        anotherAccount.setName("anotherAccount");
        anotherAccount.setCreator(anotherUser);
        accountDao.persist(anotherAccount);

        List<Account> accounts = accountService.getAllAccounts();

        assertEquals(2, accounts.size());
    }

    @Test
    public void getAccountByIdReturnsCorrectAccount() {
        Account foundAccount = accountService.getAccountById(testAccount.getId());

        assertEquals(testAccount.getId(), foundAccount.getId());
        assertEquals(testAccount.getName(), foundAccount.getName());
    }

    @Test
    public void getAccountByIdThrowsExceptionIfAccountNotFound() {
        int invalidAccountId = 999;
        assertThrows(AccountNotFoundException.class, () ->
                accountService.getAccountById(invalidAccountId));
    }

    @Test
    public void deleteAccountByIdDeletesAccount() {
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        anotherUser.setPassword("password");
        anotherUser.setEmail("parek@gmail.com");
        userDao.persist(anotherUser);

        Account anotherAccount = new Account();
        anotherAccount.setName("anotherAccount");
        anotherAccount.setCreator(anotherUser);
        accountDao.persist(anotherAccount);

        accountService.deleteAccountById(anotherAccount.getId());

        Account deletedAccount = accountDao.find(anotherAccount.getId());
        assertNull(deletedAccount);
    }

    @Test
    public void deleteAccountByIdThrowsExceptionIfAccountNotFound() {
        int invalidAccountId = 999;
        assertThrows(AccountNotFoundException.class, () ->
                accountService.deleteAccountById(invalidAccountId));
    }
}

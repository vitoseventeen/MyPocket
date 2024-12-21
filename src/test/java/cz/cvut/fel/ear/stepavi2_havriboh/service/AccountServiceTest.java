package cz.cvut.fel.ear.stepavi2_havriboh.service;



import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    private TestEntityManager em;

    @SpyBean
    private UserDao userDao;

    @SpyBean
    private AccountDao accountDao;

    @Autowired
    private AccountService accountService;

    private User user;
    private Account account;

    @BeforeEach
    public void setUp() {
        this.user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("testMail@gmail.com");
        user.setRole(Role.USER);

        this.account = new Account();
        account.setAccountName("testAccount");
        account.setBalance(BigDecimal.valueOf(100));
        account.setCurrency("CZK");

        em.persist(user);
        em.persist(account);
    }

    @Test
    public void createAccountCreatesAccountWithValidData() {
        accountService.createAccount("Savings", BigDecimal.valueOf(500), "USD");

        Account createdAccount = accountDao.findAll().stream()
                .filter(acc -> acc.getAccountName().equals("Savings"))
                .findFirst()
                .orElse(null);

        assertNotNull(createdAccount, "Account should be persisted in the database");
        assertEquals("Savings", createdAccount.getAccountName());
        assertEquals(BigDecimal.valueOf(500), createdAccount.getBalance());
        assertEquals("USD", createdAccount.getCurrency());
    }

    @Test
    public void createAccountThrowsExceptionIfBalanceIsNegative() {
        assertThrows(NegativeBalanceException.class, () ->
                accountService.createAccount("MyAccount", BigDecimal.valueOf(-100), "CZK"));
    }

    @Test
    public void createAccountThrowsExceptionIfNameIsEmpty() {
        assertThrows(EmptyNameException.class, () ->
                accountService.createAccount("", BigDecimal.valueOf(100), "CZK"));
    }

    @Test
    public void createAccountThrowsExceptionIfCurrencyIsEmpty() {
        assertThrows(EmptyCurrencyException.class, () ->
                accountService.createAccount("MyAccount", BigDecimal.valueOf(100), ""));
    }

    @Test
    public void addUserToAccountByIdAddsUserIfNotAlreadyAdded() {
        accountService.addUserToAccountById(user.getId(), account.getId());

        Account updatedAccount = accountDao.find(account.getId());
        assertTrue(updatedAccount.getUsers().contains(user));

        verify(accountDao).update(updatedAccount);
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
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        anotherUser.setPassword("anotherPassword");
        anotherUser.setEmail("anotherMail@gmail.com");
        anotherUser.setRole(Role.USER);
        em.persist(anotherUser);

        account.getUsers().add(user);
        account.getUsers().add(anotherUser);

        Account updatedAccount = em.find(Account.class, account.getId());
        assertTrue(updatedAccount.getUsers().contains(user));
        assertTrue(updatedAccount.getUsers().contains(anotherUser));

        accountService.removeUserFromAccountById(user.getId(), account.getId());

        updatedAccount = em.find(Account.class, account.getId());
        assertFalse(updatedAccount.getUsers().contains(user));
        assertTrue(updatedAccount.getUsers().contains(anotherUser));

        verify(accountDao).update(updatedAccount);
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
        anotherAccount.setAccountName("anotherAccount");
        anotherAccount.setBalance(BigDecimal.valueOf(50));
        anotherAccount.setCurrency("USD");
        em.persist(anotherAccount);

        List<Account> accounts = accountService.getAllAccounts();

        assertEquals(2, accounts.size());
    }

    @Test
    public void getAccountByIdReturnsCorrectAccount() {
        Account foundAccount = accountService.getAccountById(account.getId());

        assertEquals(account.getId(), foundAccount.getId());
        assertEquals(account.getAccountName(), foundAccount.getAccountName());
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
        newAccount.setAccountName("AccountToDelete");
        newAccount.setBalance(BigDecimal.valueOf(100));
        newAccount.setCurrency("CZK");
        em.persist(newAccount);

        accountService.deleteAccountById(newAccount.getId());

        Account deletedAccount = em.find(Account.class, newAccount.getId());
        assertNull(deletedAccount);
    }

    @Test
    public void deleteAccountByIdThrowsExceptionIfAccountNotFound() {
        int invalidAccountId = 999;
        assertThrows(AccountNotFoundException.class, () ->
                accountService.deleteAccountById(invalidAccountId));
    }
}

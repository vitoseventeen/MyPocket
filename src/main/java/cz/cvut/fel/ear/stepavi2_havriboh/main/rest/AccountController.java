package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.AccountNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.UserAlreadyInAccountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.UserNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.AccountService;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.SystemInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        logger.info("Fetched {} accounts", accounts.size());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountById(@PathVariable("id") int id) {
        try {
            if (!accountService.getAccountById(id).getUsers().contains(SecurityUtils.getCurrentUser())) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            Account account = accountService.getAccountById(id);
            logger.info("Fetched account with id: {}", id);
            return ResponseEntity.ok(account);
        } catch (AccountNotFoundException e) {
            logger.error("Account not found with id: {}", id);
            return ResponseEntity.status(404).body("Account not found");
        }
    }

    @PreAuthorize("hasAnyRole('USER','PREMIUM')")
    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody Account account) {
        try {
            accountService.createAccountWithBudget(account.getName(), account.getBudget().getBalance(), account.getBudget().getCurrency().toString());
            logger.info("Created account with name: {}", account.getName());
            return ResponseEntity.status(201).body("Account created");

        } catch (Exception e) {
            logger.error("Error creating account: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error creating account");
        }
    }


    // only creator of account can delete it
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAccount(@PathVariable("id") int id) {
        try {
            if (accountService.getAccountById(id).getCreator().equals(SecurityUtils.getCurrentUser())) {
                accountService.deleteAccountById(id);
                logger.info("Deleted account with id: {}", id);
                return ResponseEntity.ok("Account deleted");
            }
            return ResponseEntity.status(403).body("Forbidden");
        } catch (AccountNotFoundException e) {
            logger.error("Account not found with id: {}", id);
            return ResponseEntity.status(404).body("Account not found");
        }
    }

    @PostMapping("/{accountId}/addUser/{userId}")
    public ResponseEntity<Object> addUserToAccount(@PathVariable("userId") int userId, @PathVariable("accountId") int accountId) {
        try {
            if (accountService.getAccountById(accountId).getCreator().equals(SecurityUtils.getCurrentUser())) {
                accountService.addUserToAccountById(userId, accountId);
                logger.info("Added user {} to account {}", userId, accountId);
                return ResponseEntity.ok("User added to account");
            }
            return ResponseEntity.status(403).body("Forbidden");
        } catch (AccountNotFoundException | UserNotFoundException | UserAlreadyInAccountException e) {
            logger.error("Error adding user to account: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{accountId}/removeUser/{userId}")
    public ResponseEntity<Object> removeUserFromAccount(@PathVariable("userId") int userId, @PathVariable("accountId") int accountId) {
        try {
            if (accountService.getAccountById(accountId).getCreator().equals(SecurityUtils.getCurrentUser())) {
                accountService.removeUserFromAccountById(userId, accountId);
                logger.info("Removed user {} from account {}", userId, accountId);
                return ResponseEntity.ok("User removed from account");
            }
            return ResponseEntity.status(403).body("Forbidden");
        } catch (AccountNotFoundException | UserNotFoundException e) {
            logger.error("Error removing user from account: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

}

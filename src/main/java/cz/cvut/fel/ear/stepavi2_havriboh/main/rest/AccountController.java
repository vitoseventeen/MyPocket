package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.AccountNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.UserAlreadyInAccountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.UserNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.AccountService;
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

    private boolean isOwnerOrAdmin(int accountId) {
        Account account = accountService.getAccountById(accountId);
        boolean isOwner = account.getCreator().equals(SecurityUtils.getCurrentUser());
        boolean isAdmin = SecurityUtils.getCurrentUser().isAdmin();
        return !isOwner && !isAdmin;
    }

    private boolean isMemberOrAdmin(int accountId) {
        Account account = accountService.getAccountById(accountId);
        boolean isMember = account.getUsers().contains(SecurityUtils.getCurrentUser());
        boolean isAdmin = SecurityUtils.getCurrentUser().isAdmin();
        return !isMember && !isAdmin;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        logger.info("Fetched {} accounts", accounts.size());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountById(@PathVariable("id") int id) {
        try {
            if (isMemberOrAdmin(id)) {
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

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_PREMIUM','ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody Account account) {
        try {
            accountService.createAccountWithBudget(
                    account.getName(),
                    account.getBudget().getBalance(),
                    account.getBudget().getCurrency().toString()
            );
            logger.info("Created account with name: {}", account.getName());
            return ResponseEntity.status(201).body("Account created");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid currency provided: {}", e.getMessage());
            return ResponseEntity.status(400).body("Invalid currency: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating account: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error creating account");
        }
    }


    // only creator of account can delete it
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAccount(@PathVariable("id") int id) {
        try {
            if (isOwnerOrAdmin(id)) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            accountService.deleteAccountById(id);
            logger.info("Deleted account with id: {}", id);
            return ResponseEntity.ok("Account deleted");
        } catch (AccountNotFoundException e) {
            logger.error("Account not found with id: {}", id);
            return ResponseEntity.status(404).body("Account not found");
        }
    }

    @PostMapping("/addUser/{userId}/to/{accountId}")
    public ResponseEntity<Object> addUserToAccount(@PathVariable("userId") int userId, @PathVariable("accountId") int accountId) {
        try {
            if (isOwnerOrAdmin(accountId)) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            accountService.addUserToAccountById(userId, accountId);
            logger.info("Added user {} to account {}", userId, accountId);
            return ResponseEntity.ok("User added to account");

        } catch (AccountNotFoundException | UserNotFoundException | UserAlreadyInAccountException e) {
            logger.error("Error adding user to account: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/removeUser/{userId}/from/{accountId}")
    public ResponseEntity<Object> removeUserFromAccount(@PathVariable("userId") int userId, @PathVariable("accountId") int accountId) {
        try {
            if (isOwnerOrAdmin(accountId)) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            accountService.removeUserFromAccountById(userId, accountId);
            logger.info("Removed user {} from account {}", userId, accountId);
            return ResponseEntity.ok("User removed from account");
        } catch (AccountNotFoundException | UserNotFoundException e) {
            logger.error("Error removing user from account: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

}

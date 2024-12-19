package cvut.ear.stepavi2_havriboh.main.rest;

import cvut.ear.stepavi2_havriboh.main.exception.AccountNotFoundException;
import cvut.ear.stepavi2_havriboh.main.model.Account;
import cvut.ear.stepavi2_havriboh.main.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<Object> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        logger.info("Fetched {} accounts", accounts.size());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountById(@PathVariable("id") int id) {
        try {
            Account account = accountService.getAccountById(id);
            logger.info("Fetched account with id: {}", id);
            return ResponseEntity.ok(account);
        } catch (AccountNotFoundException e) {
            logger.error("Account not found with id: {}", id);
            return ResponseEntity.status(404).body("Account not found");
        }
    }

    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody Account account) {
        try {
            accountService.createAccount(account.getAccountName(), account.getBalance(), account.getCurrency());
            logger.info("Created account with name: {}", account.getAccountName());
            return ResponseEntity.status(201).body("Account created");
        } catch (Exception e) {
            logger.error("Error creating account: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error creating account");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAccount(@PathVariable("id") int id) {
        try {
            accountService.deleteAccountById(id);
            logger.info("Deleted account with id: {}", id);
            return ResponseEntity.ok("Account deleted");
        } catch (AccountNotFoundException e) {
            logger.error("Account not found with id: {}", id);
            return ResponseEntity.status(404).body("Account not found");
        }
    }

}

package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;


import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.TransactionNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/rest/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_PREMIUM')")
    @PostMapping
    public ResponseEntity<Object> createTransaction(
            @RequestBody Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currencyStr = (String) request.get("currency");
            Currency currency = Currency.valueOf(currencyStr);
            LocalDate date = LocalDate.parse(request.get("date").toString());
            String description = (String) request.get("description");
            TransactionType type = TransactionType.valueOf((String) request.get("type"));
            int accountId = Integer.parseInt(request.get("accountId").toString());
            int categoryId = Integer.parseInt(request.get("categoryId").toString());

            transactionService.createTransaction(amount, currency, date, description, type, accountId, categoryId);
            logger.info("Created transaction for account id: {}", accountId);
            return ResponseEntity.status(201).body("Transaction created");
        } catch (Exception e) {
            logger.error("Error creating transaction: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error creating transaction: " + e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_PREMIUM')")
    @PostMapping("/recurring")
    public ResponseEntity<Object> createRecurringTransaction(@RequestBody Map<String, Object> rawParams) {
        try {
            BigDecimal amount = new BigDecimal((String) rawParams.get("amount"));
            Currency currency = Currency.valueOf((String) rawParams.get("currency"));
            LocalDate date = LocalDate.parse((String) rawParams.get("date"));
            String description = (String) rawParams.get("description");
            TransactionType type = TransactionType.valueOf((String) rawParams.get("type"));
            int accountId = Integer.parseInt((String) rawParams.get("accountId"));
            int categoryId = Integer.parseInt((String) rawParams.get("categoryId"));
            int interval = Integer.parseInt((String) rawParams.get("interval"));
            TransactionIntervalType intervalUnit = TransactionIntervalType.valueOf((String) rawParams.get("intervalUnit"));

            transactionService.createRecurringTransaction(amount, currency, date, description, type, accountId, categoryId, interval, intervalUnit);

            logger.info("Created recurring transaction for account id: {}", accountId);
            return ResponseEntity.status(201).body("Recurring transactions created");
        } catch (Exception e) {
            logger.error("Error creating recurring transaction: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error creating recurring transaction: " + e.getMessage());
        }
    }



    private boolean checkTransactionPerms(int id) {
        User currentUser = Objects.requireNonNull(SecurityUtils.getCurrentUser(), "Current user cannot be null.");

        return transactionService.getTransactionById(id)
                .getAccount()
                .getUsers()
                .contains(currentUser) || currentUser.isAdmin();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getTransactionById(@PathVariable("id") int id) {
        logger.info("Fetching transaction with ID: {}", id);
        try {
            if (!checkTransactionPerms(id)) {
                throw new AccessDeniedException("You do not have permission to access this resource.");
            }
            Transaction transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok().body(transaction);
        } catch (TransactionNotFoundException e) {
            logger.error("Transaction not found with ID: {}", id);
            return ResponseEntity.status(404).body("Transaction not found");
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable("id") int id) {
        try {

            transactionService.deleteTransactionById(id);
            logger.info("Deleted transaction with id: {}", id);
            return ResponseEntity.ok("Transaction deleted");
        } catch (TransactionNotFoundException e) {
            logger.error("Transaction not found with id: {}", id);
            return ResponseEntity.status(404).body("Transaction not found");
        }
    }

    @PutMapping("/{id}")
    @PostMapping
    public ResponseEntity<Object> updateTransaction( @PathVariable("id") int id,
            @RequestBody Map<String, Object> request) {
        try {
            checkTransactionPerms(id);
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currencyStr = (String) request.get("currency");
            Currency currency = Currency.valueOf(currencyStr);
            LocalDate date = LocalDate.parse(request.get("date").toString());
            String description = (String) request.get("description");
            TransactionType type = TransactionType.valueOf((String) request.get("type"));
            int accountId = Integer.parseInt(request.get("accountId").toString());
            int categoryId = Integer.parseInt(request.get("categoryId").toString());

            transactionService.updateTransaction(
                    id,
                    amount,
                    currency,
                    date,
                    description,
                    type,
                    accountId,
                    categoryId
            );

            logger.info("Updated transaction with id: {}", id);
            return ResponseEntity.ok("Transaction updated");
        } catch (TransactionNotFoundException e) {
            logger.error("Transaction not found with id: {}", id);
            return ResponseEntity.status(404).body("Transaction not found");
        } catch (Exception e) {
            logger.error("Error updating transaction with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(400).body("Error updating transaction: " + e.getMessage());
        }
    }



}

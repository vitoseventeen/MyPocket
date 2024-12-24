package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;


import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.TransactionNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Currency;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Transaction;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.TransactionIntervalType;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.TransactionType;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rest/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'PREMIUM')")
    @PostMapping
    public ResponseEntity<Object> createTransaction(@RequestBody Transaction transaction) {
        try {
            transactionService.createTransaction(
                    transaction.getAmount(), transaction.getBudget().getCurrency(), transaction.getDate(),
                    transaction.getDescription(),transaction.getType(),transaction.getAccount().getId(),
                    transaction.getCategory().getId());
            logger.info("Created transaction for account id: {}", transaction.getAccount().getId());
            return ResponseEntity.status(201).body("Transaction created");
        } catch (Exception e) {
            logger.error("Error creating transaction: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error creating transaction: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'PREMIUM')")
    @PostMapping("/recurring")
    public ResponseEntity<Object> createRecurringTransaction(@RequestParam BigDecimal amount,
                                                             @RequestParam Currency currency,
                                                             @RequestParam LocalDate date,
                                                             @RequestParam String description,
                                                             @RequestParam TransactionType type,
                                                             @RequestParam int accountId,
                                                             @RequestParam int categoryId,
                                                             @RequestParam int interval,
                                                             @RequestParam TransactionIntervalType intervalUnit) {
        try {
            transactionService.createRecurringTransaction(amount, currency, date, description, type, accountId, categoryId, interval, intervalUnit);
            logger.info("Created recurring transaction for account id: {}", accountId);
            return ResponseEntity.status(201).body("Recurring transactions created");
        } catch (Exception e) {
            logger.error("Error creating recurring transaction: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error creating recurring transaction: " + e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getTransactionById(@PathVariable("id") int id) {
        logger.info("Fetching transaction with ID: {}", id);
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok().body(transaction);
        } catch (TransactionNotFoundException e) {
            logger.error("Transaction not found with ID: {}", id);
            return ResponseEntity.status(404).body("Transaction not found with ID: " + id);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTransaction(
            @PathVariable("id") int id,
            @RequestBody Transaction transaction) {
        try {
            transactionService.updateTransaction(
                    id,
                    transaction.getAmount(), transaction.getBudget().getCurrency(), transaction.getDate(),
                    transaction.getDescription(),transaction.getType(),transaction.getAccount().getId(),
                    transaction.getCategory().getId()
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

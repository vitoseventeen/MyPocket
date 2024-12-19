package cz.cvut.sem.ear.stepavi2.havriboh.main.rest;


import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.CategoryNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.TransactionNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.UserNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import cz.cvut.sem.ear.stepavi2.havriboh.main.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTransactionById(@PathVariable("id") int id) {
        try {
            Transaction transaction = transactionService.getTransactionsById(id);
            return ResponseEntity.ok(transaction);
        } catch (TransactionNotFoundException e) {
            logger.error("Transaction not found with id: {}", id);
            return ResponseEntity.status(404).body("Transaction not found");
        }
    }

    @GetMapping("/{categoryId}/total")
    public ResponseEntity<Object> getTotalSpentByCategoryId(@PathVariable("categoryId") int categoryId) {
        try {
            BigDecimal totalSpent = transactionService.getTotalSpentByCategoryId(categoryId);
            return ResponseEntity.ok(totalSpent);
        } catch (CategoryNotFoundException e) {
            logger.error("Category not found with id: {}", categoryId);
            return ResponseEntity.status(404).body("Category not found");
        }
    }

    @PostMapping
    public ResponseEntity<Object> createTransaction(@RequestBody Transaction transaction) {
        try {
            transactionService.createTransaction(transaction.getAmount(), transaction.getDate(), transaction.getDescription(),
                    transaction.getType(), transaction.getUser().getId(), transaction.getAccount().getId(), transaction.getCategory().getId());
            logger.info("Created transaction for user id: {}", transaction.getUser().getId());
            return ResponseEntity.status(201).body("Transaction created");
        } catch (Exception e) {
            logger.error("Error creating transaction: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error creating transaction: " + e.getMessage());
        }
    }


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
    public ResponseEntity<Object> updateTransaction(
            @PathVariable("id") int id,
            @RequestBody Transaction transaction) {
        try {
            transactionService.updateTransaction(
                    id,
                    transaction.getAmount(),
                    transaction.getDate(),
                    transaction.getDescription(),
                    transaction.getType()
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

package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;


import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.BudgetNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.CategoryNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/rest/budgets")
public class BudgetController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetController.class);
    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllBudgets() {
        List<Budget> budgets = budgetService.getAllBudgets();
        logger.info("Fetched {} budgets", budgets.size());
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBudgetById(@PathVariable("id") int id) {
        try {
            Budget budget = budgetService.getBudgetById(id);
            logger.info("Fetched budget with id: {}", id);
            return ResponseEntity.ok(budget);
        } catch (BudgetNotFoundException e) {
            logger.error("Budget not found with id: {}", id);
            return ResponseEntity.status(404).body("Budget not found");
        }
    }

    @PutMapping("/{id}/increase")
    public ResponseEntity<Object> increaseBudget(@PathVariable("id") int id, @RequestBody double amount, @RequestBody String currency) {
        try {
            budgetService.increaseBudget(id, BigDecimal.valueOf(amount), currency);
            logger.info("Increased budget with id: {}", id);
            return ResponseEntity.ok("Budget increased");
        } catch (BudgetNotFoundException | NegativeAmountException e) {
            logger.error("Error increasing budget: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error increasing budget: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/decrease")
    public ResponseEntity<Object> decreaseBudget(@PathVariable("id") int id, @RequestBody double amount, @RequestBody String currency) {
        try {
            budgetService.decreaseBudget(id, BigDecimal.valueOf(amount), currency);
            logger.info("Decreased budget with id: {}", id);
            return ResponseEntity.ok("Budget decreased");
        } catch (BudgetNotFoundException | NegativeAmountException e) {
            logger.error("Error decreasing budget: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error decreasing budget: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBudget(@PathVariable("id") int id) {
        try {
            budgetService.deleteBudgetById(id);
            logger.info("Deleted budget with id: {}", id);
            return ResponseEntity.ok("Budget deleted");
        } catch (BudgetNotFoundException e) {
            logger.error("Budget not found with id: {}", id);
            return ResponseEntity.status(404).body("Budget not found");
        }
    }
}

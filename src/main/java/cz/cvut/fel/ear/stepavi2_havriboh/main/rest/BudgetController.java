package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;


import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.BudgetNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/rest/budgets")
public class BudgetController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetController.class);
    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getAllBudgets() {
        List<Budget> budgets = budgetService.getAllBudgets();
        logger.info("Fetched {} budgets", budgets.size());
        return ResponseEntity.ok(budgets);
    }

    private boolean isMemberOrAdmin(int budgetId) {
        User user = SecurityUtils.getCurrentUser();
        Budget budget = budgetService.getBudgetById(budgetId);
        assert user != null;
        return budget.getAccount().getMemberUsernames().contains(user.getUsername()) || user.isAdmin();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getBudgetById(@PathVariable("id") int id) {
        try {
            if (!isMemberOrAdmin(id)) {
                return ResponseEntity.status(403).body("Access denied");
            }
            Budget budget = budgetService.getBudgetById(id);
            logger.info("Fetched budget with id: {}", id);
            return ResponseEntity.ok(budget);
        } catch (BudgetNotFoundException e) {
            logger.error("Budget not found with id: {}", id);
            return ResponseEntity.status(404).body("Budget not found");
        }
    }
    @PutMapping("/{id}/increase")
    public ResponseEntity<Object> increaseBudget(@PathVariable("id") int id, @RequestBody Map<String, Object> request) {
        try {
            if (!isMemberOrAdmin(id)) {
                return ResponseEntity.status(403).body("Access denied");
            }
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = (String) request.get("currency");
            budgetService.increaseBudget(id, amount, currency);
            logger.info("Increased budget with id: {}", id);
            return ResponseEntity.ok("Budget increased");
        } catch (BudgetNotFoundException | NegativeAmountException e) {
            logger.error("Error increasing budget: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error increasing budget: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/decrease")
    public ResponseEntity<Object> decreaseBudget(@PathVariable("id") int id, @RequestBody Map<String, Object> request) {
        try {
            if (!isMemberOrAdmin(id)) {
                return ResponseEntity.status(403).body("Access denied");
            }
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = (String) request.get("currency");
            budgetService.decreaseBudget(id, amount, currency);
            logger.info("Decreased budget with id: {}", id);
            return ResponseEntity.ok("Budget decreased");
        } catch (BudgetNotFoundException | NegativeAmountException e) {
            logger.error("Error decreasing budget: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error decreasing budget: " + e.getMessage());
        }
    }
}

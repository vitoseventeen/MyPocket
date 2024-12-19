package cvut.ear.stepavi2_havriboh.main.service;

import cvut.ear.stepavi2_havriboh.main.dao.BudgetDao;
import cvut.ear.stepavi2_havriboh.main.dao.CategoryDao;
import cvut.ear.stepavi2_havriboh.main.exception.BudgetNotFoundException;
import cvut.ear.stepavi2_havriboh.main.exception.CategoryNotFoundException;
import cvut.ear.stepavi2_havriboh.main.exception.EmptyCurrencyException;
import cvut.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cvut.ear.stepavi2_havriboh.main.model.Budget;
import cvut.ear.stepavi2_havriboh.main.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetService {
    private final BudgetDao budgetDao;
    private final CategoryDao categoryDao;

    @Autowired
    public BudgetService(BudgetDao budgetDao, CategoryDao categoryDao) {
        this.budgetDao = budgetDao;
        this.categoryDao = categoryDao;
    }


    @Transactional(readOnly = true)
    public BigDecimal getTotalRemainingFunds() {
        List<Budget> budgets = budgetDao.findAll();
        return budgets.stream()
                .map(budget -> budget.getTargetAmount().subtract(budget.getCurrentAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public List<Budget> getAllBudgets() {
        return budgetDao.findAll();
    }

    @Transactional
    public void createBudgetForCategoryById(int categoryId, BigDecimal targetAmount, String currency) {
        Category category = categoryDao.find(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException("Category not found");
        }

        if (targetAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Target amount must be positive");
        }

        if (currency == null) {
            throw new EmptyCurrencyException("Currency must be specified");
        }

        Budget budget = new Budget();
        budget.setTargetAmount(targetAmount);
        budget.setCurrentAmount(BigDecimal.ZERO);
        budget.setCurrency(currency);
        if (category.getBudget() != null) {
            throw new IllegalArgumentException("Category already has a budget");
        }
        budget.setCategory(category);

        budgetDao.persist(budget);
    }

    @Transactional
    public void increaseBudget(int budgetId, BigDecimal amount) {
        Budget budget = budgetDao.find(budgetId);
        if (budget == null) {
            throw new BudgetNotFoundException("Budget not found");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Amount must be positive");
        }

        budget.increaseBudget(amount);

        budgetDao.update(budget);
    }

    @Transactional
    public void decreaseBudget(int budgetId, BigDecimal amount) {
        Budget budget = budgetDao.find(budgetId);
        if (budget == null) {
            throw new BudgetNotFoundException("Budget not found");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Amount must be positive");
        }
        if (amount.compareTo(budget.getCurrentAmount()) > 0) {
            throw new NegativeAmountException("Amount must be less than current amount");
        }

        budget.decreaseBudget(amount);

        budgetDao.update(budget);
    }

    @Transactional(readOnly = true)
    public Budget getBudgetById(int id) {
        Budget budget = budgetDao.find(id);
        if (budget == null) {
            throw new BudgetNotFoundException("Budget not found");
        }
        return budget;
    }

    @Transactional(readOnly = true)
    public BigDecimal getRemainingLimit(int budgetId) {
        Budget budget = budgetDao.find(budgetId);
        if (budget == null) {
            throw new BudgetNotFoundException("Budget not found");
        }
        return budget.getTargetAmount().subtract(budget.getCurrentAmount());
    }


    @Transactional
    public void deleteBudgetById(int id) {
        Budget budget = budgetDao.find(id);
        if (budget == null) {
            throw new BudgetNotFoundException("Budget not found");
        }

        budgetDao.remove(budget);
    }



}

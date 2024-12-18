package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.BudgetDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.CategoryDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.BudgetNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.CategoryNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.EmptyCurrencyException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.NegativeAmountException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Budget;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Category;
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

    @Transactional
    public void transferFundsById(int fromBudgetId, int toBudgetId, BigDecimal amount) {
        Budget fromBudget = budgetDao.find(fromBudgetId);
        Budget toBudget = budgetDao.find(toBudgetId);

        if (fromBudget == null || toBudget == null) {
            throw new BudgetNotFoundException("One or both budgets not found");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Amount must be positive");
        }

        fromBudget.decreaseBudget(amount);
        toBudget.increaseBudget(amount);

        budgetDao.update(fromBudget);
        budgetDao.update(toBudget);
    }

    // show remaining funds in all budgets
    @Transactional(readOnly = true)
    public BigDecimal getTotalRemainingFunds() {
        List<Budget> budgets = budgetDao.findAll();
        return budgets.stream()
                .map(budget -> budget.getTargetAmount().subtract(budget.getCurrentAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Transactional(readOnly = true)
    public Budget getBudgetByCategoryId(int categoryId) {
        Category category = categoryDao.find(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        return category.getBudget();
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

    // nemuze byt mensi nez 0
    @Transactional
    public void decreaseBudget(int budgetId, BigDecimal amount) {
        Budget budget = budgetDao.find(budgetId);
        if (budget == null) {
            throw new BudgetNotFoundException("Budget not found");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Amount must be positive");
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

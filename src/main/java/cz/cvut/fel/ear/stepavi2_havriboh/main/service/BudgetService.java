package cz.cvut.fel.ear.stepavi2_havriboh.main.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.BudgetDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.BudgetNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NegativeAmountException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.UnsupportedCurrencyException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Currency;
import cz.cvut.fel.ear.stepavi2_havriboh.main.utils.CurrencyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetService {
    private final BudgetDao budgetDao;
    private final AccountDao accountDao;
    private final List<Currency> currencies = List.of(Currency.values());

    @Autowired
    public BudgetService(BudgetDao budgetDao, AccountDao accountDao) {
        this.budgetDao = budgetDao;
        this.accountDao = accountDao;
    }

    @Transactional(readOnly = true)
    public List<Budget> getAllBudgets() {
        return budgetDao.findAll();
    }

    protected void validBudget(String currency, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Amount must be positive and non-null");
        }
        if (currency == null || currency.isEmpty()) {
            throw new UnsupportedCurrencyException("Currency cannot be null or empty");
        }
        try {
            Currency.fromString(currency);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedCurrencyException("Currency not found: " + currency);
        }
    }

    @Transactional
    public void increaseBudget(int budgetId, BigDecimal amount, String currency) {
        Budget budget = budgetDao.find(budgetId);
        validBudget(currency, amount);
        // calculate currency changes
        if (budget.getCurrency().toString().equals(currency)) {
            budget.setBalance(budget.getBalance().add(amount));
        } else {
            BigDecimal convertedAmount = CurrencyConverter.convert(amount, currency, budget.getCurrency().toString());
            budget.setBalance(budget.getBalance().add(convertedAmount));
        }
        budgetDao.update(budget);
    }

    @Transactional
    public void decreaseBudget(int budgetId, BigDecimal amount, String currency) {
        Budget budget = budgetDao.find(budgetId);
        validBudget(currency, amount);
        if (budget.getCurrency().toString().equals(currency)) {
            budget.setBalance(budget.getBalance().subtract(amount));
        } else {
            BigDecimal convertedAmount = CurrencyConverter.convert(amount, currency, budget.getCurrency().toString());
            budget.setBalance(budget.getBalance().subtract(convertedAmount));
        }
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

}

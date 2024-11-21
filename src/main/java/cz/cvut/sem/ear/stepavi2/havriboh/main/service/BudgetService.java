package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.BudgetDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.BudgetLimitExceededException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Budget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
    private final BudgetDao budgetDao;

    @Autowired
    public BudgetService(BudgetDao budgetDao) {
        this.budgetDao = budgetDao;
    }

    public void checkBudgetLimit(Budget budget) {
        if (budget.getCurrentAmount().compareTo(budget.getTargetAmount()) > 0) {
            throw new BudgetLimitExceededException("Budget limit exceeded");
        }
    }

    public void createBudget(Budget budget) {
        budgetDao.persist(budget);
    }



}

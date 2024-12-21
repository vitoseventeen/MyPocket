package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetDao extends BaseDao<Budget> {
    public BudgetDao() {
        super(Budget.class);
    }
}

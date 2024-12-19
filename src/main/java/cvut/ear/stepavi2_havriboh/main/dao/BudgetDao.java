package cvut.ear.stepavi2_havriboh.main.dao;

import cvut.ear.stepavi2_havriboh.main.model.Budget;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetDao extends BaseDao<Budget> {
    public BudgetDao() {
        super(Budget.class);
    }
}

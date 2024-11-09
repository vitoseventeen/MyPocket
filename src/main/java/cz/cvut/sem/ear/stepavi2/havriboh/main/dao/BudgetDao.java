package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Budget;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetDao extends BaseDao<Budget> {
    public BudgetDao() {
        super(Budget.class);
    }

    public Budget getBudgetByUserId(int userId) {
        return em.createQuery("SELECT b FROM Budget b WHERE b.user.id = :userId", Budget.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}

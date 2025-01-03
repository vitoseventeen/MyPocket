package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import org.springframework.stereotype.Repository;
@Repository
public class BudgetDao extends BaseDao<Budget> {
    public BudgetDao() {
        super(Budget.class);
    }

    public Budget findByAccountId(int accountId) {
        return em.createNamedQuery("Budget.findByAccountId", Budget.class)
                .setParameter("accountId", accountId)
                .getSingleResult();
    }

    public Budget findByTransactionId(int transactionId) {
        return em.createNamedQuery("Budget.findByTransactionId", Budget.class)
                .setParameter("transactionId", transactionId)
                .getSingleResult();
    }

    public Budget findById(int id) {
        return em.createNamedQuery("Budget.findById", Budget.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}

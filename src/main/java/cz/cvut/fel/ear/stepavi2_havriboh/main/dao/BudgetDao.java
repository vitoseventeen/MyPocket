package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Budget;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetDao extends BaseDao<Budget> {
    public BudgetDao() {
        super(Budget.class);
    }

    public Budget findByAccountId(int id) {
        return em.createQuery("SELECT b FROM Budget b WHERE b.account.id = :id", Budget.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public Budget findByTransactionId(int id) {
        return em.createQuery("SELECT b FROM Budget b WHERE b.transactions.id = :id", Budget.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public Budget findById(int id) {
        return em.createQuery("SELECT b FROM Budget b WHERE b.id = :id", Budget.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}

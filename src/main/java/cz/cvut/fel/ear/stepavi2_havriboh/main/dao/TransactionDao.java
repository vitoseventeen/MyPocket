package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Transaction;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionDao extends BaseDao<Transaction> {
    public TransactionDao() {
        super(Transaction.class);
    }

    public List<Transaction> findByAccount(int accountId) {
        return em.createNamedQuery("Transaction.findByAccount", Transaction.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return em.createNamedQuery("Transaction.findByDateRange", Transaction.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    public List<Transaction> findByCategory(int categoryId) {
        return em.createNamedQuery("Transaction.findByCategory", Transaction.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    public List<Transaction> findByBudget(int budgetId) {
        return em.createNamedQuery("Transaction.findByBudget", Transaction.class)
                .setParameter("budgetId", budgetId)
                .getResultList();
    }
}

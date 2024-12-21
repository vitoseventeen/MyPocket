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

    @Override
    public void remove(Transaction transaction) {
        if (em.contains(transaction)) {
            em.remove(transaction);
        } else {
            em.remove(em.merge(transaction));
        }
    }

    @Override
    public void persist(Transaction transaction) {
        super.persist(transaction);
    }


    public Optional<Transaction> findTransactionById(int id) {
        try {
            return Optional.of(
                    em.createQuery("SELECT t FROM Transaction t WHERE t.id = :id", Transaction.class)
                            .setParameter("id", id)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Transaction> findTransactionsByCategory(Category category) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.category = :category", Transaction.class)
                .setParameter("category", category)
                .getResultList();
    }

    public Optional<BigDecimal> getTotalSpentByCategory(Category category) {
        try {
            return Optional.ofNullable(
                    em.createQuery("SELECT SUM(t.amount) FROM Transaction t WHERE t.category = :category", BigDecimal.class)
                            .setParameter("category", category)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Transaction> findById(int id) {
        try {
            return Optional.of(
                    em.createQuery("SELECT t FROM Transaction t WHERE t.id = :id", Transaction.class)
                            .setParameter("id", id)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Transaction> findTransactionsByAccountAndDateRange(Account account, LocalDate fromDate, LocalDate toDate) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.account = :account AND t.date >= :fromDate AND t.date <= :toDate", Transaction.class)
                .setParameter("account", account)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();
    }
}

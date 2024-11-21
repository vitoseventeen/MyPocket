package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Category;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionDao extends BaseDao<Transaction> {
    public TransactionDao() {
        super(Transaction.class);
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

    public List<Transaction> findTransactionsByUser(User user) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.user = :user", Transaction.class)
                .setParameter("user", user)
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
}

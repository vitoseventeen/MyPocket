package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Category;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public class TransactionDao extends BaseDao<Transaction> {
    public TransactionDao() {
        super(Transaction.class);
    }

    public void save(Transaction transaction) {
        em.persist(transaction);
    }

    public List<Transaction> findAll() {
        return em.createQuery("SELECT t FROM Transaction t", Transaction.class)
                .getResultList();
    }

    public List<Transaction> findTransactionsByUser(User user) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.user = :user", Transaction.class)
                .setParameter("user", user)
                .getResultList();
    }

    public List<Transaction> findTransactionsByUserWithDatesAndCategory(User user, Date fromDate, Date toDate, String categoryType) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.user = :user AND t.date BETWEEN :fromDate AND :toDate AND t.category = :categoryType", Transaction.class)
                .setParameter("user", user)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .setParameter("categoryType", categoryType)
                .getResultList();
    }

    public BigDecimal getTotalSpentByCategory(Category category) {
        return em.createQuery("SELECT SUM(t.amount) FROM Transaction t WHERE t.category = :category", BigDecimal.class)
                .setParameter("category", category)
                .getSingleResult();
    }
}

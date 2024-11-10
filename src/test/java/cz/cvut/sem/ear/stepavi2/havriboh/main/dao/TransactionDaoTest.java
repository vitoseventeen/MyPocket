package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;


import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Category;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class TransactionDaoTest {
    @Autowired
    private TransactionDao transactionDao;

    @PersistenceContext
    private EntityManager em;

    private User user;
    private Category category;
    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("testUser");
        em.persist(user);


        category = new Category();
        category.setName("Groceries");
        em.persist(category);


        transaction1 = new Transaction();
        transaction1.setUser(user);
        transaction1.setCategory(category);
        transaction1.setAmount(new BigDecimal("50.00"));
        transaction1.setDate(new Date());
        transactionDao.save(transaction1);

        transaction2 = new Transaction();
        transaction2.setUser(user);
        transaction2.setCategory(category);
        transaction2.setAmount(new BigDecimal("100.00"));
        transaction2.setDate(new Date());
        transactionDao.save(transaction2);

        em.flush();
        em.clear();
    }

    @Test
    public void testSaveTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("200.00"));
        transaction.setDate(new Date());

        transactionDao.save(transaction);

        assertNotNull(transaction.getId(), "Id should be generated.");
    }

    @Test
    public void testFindAllTransactions() {
        List<Transaction> transactions = transactionDao.findAll();
        assertEquals(2, transactions.size(), "2 transactions should be found.");
    }

    @Test
    public void testFindTransactionsByUser() {
        List<Transaction> transactions = transactionDao.findTransactionsByUser(user);
        assertEquals(2, transactions.size(), "2 transactions should be found for user 'testUser'.");
    }

    @Test
    public void testGetTotalSpentByCategory() {
        BigDecimal totalSpent = transactionDao.getTotalSpentByCategory(category);
        assertEquals(new BigDecimal("150.00"), totalSpent, "Total spent should be 150.00.");
    }
}

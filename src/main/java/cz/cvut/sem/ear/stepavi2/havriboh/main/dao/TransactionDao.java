package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDao extends BaseDao<Transaction> {
    public TransactionDao() {
        super(Transaction.class);
    }

}

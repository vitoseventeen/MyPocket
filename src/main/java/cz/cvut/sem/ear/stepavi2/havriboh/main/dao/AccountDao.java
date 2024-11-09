package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Account;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDao extends BaseDao<Account> {
    public AccountDao() {
        super(Account.class);
    }

    public Account getAccountByUserId(int userId) {
        return em.createQuery("SELECT a FROM Account a WHERE a.user.id = :userId", Account.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}

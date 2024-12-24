package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountDao extends BaseDao<Account> {
    public AccountDao() {
        super(Account.class);
    }

    public Optional<Account> findById(int id) {
        try {
            return Optional.ofNullable(
                    em.createQuery("SELECT u FROM Account u WHERE u.id = :id", Account.class)
                            .setParameter("id", id)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Account> findByName(String name) {
        try {
            return Optional.ofNullable(
                    em.createQuery("SELECT u FROM Account u WHERE u.name = :name", Account.class)
                            .setParameter("name", name)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}

package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public class AccountDao extends BaseDao<Account> {
    public AccountDao() {
        super(Account.class);
    }

    public Optional<Account> findById(int id) {
        try {
            return Optional.ofNullable(
                    em.createNamedQuery("Account.findById", Account.class)
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
                    em.createNamedQuery("Account.findByName", Account.class)
                            .setParameter("name", name)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Account> findByCreatorId(int creatorId) {
        return em.createNamedQuery("Account.findByCreatorId", Account.class)
                .setParameter("creatorId", creatorId)
                .getResultList();
    }
}

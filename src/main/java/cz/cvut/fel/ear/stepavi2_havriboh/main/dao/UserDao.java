package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDao extends BaseDao<User> {
    public UserDao() {
        super(User.class);
    }

    public void persist(User user) {
        em.persist(user);
    }

    public Optional<User> findByEmail(String email) {
        try {
            return Optional.ofNullable(
                    em.createNamedQuery("User.findByEmail", User.class)
                            .setParameter("email", email)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findById(int id) {
        try {
            return Optional.ofNullable(
                    em.createNamedQuery("User.findById", User.class)
                            .setParameter("id", id)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            return Optional.ofNullable(
                    em.createNamedQuery("User.findByUsername", User.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        return em.createNamedQuery("User.findAll", User.class).getResultList();
    }
}

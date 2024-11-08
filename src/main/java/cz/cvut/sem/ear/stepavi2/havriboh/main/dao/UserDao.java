package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends BaseDao<User> {
    public UserDao() {
        super(User.class);
    }
}

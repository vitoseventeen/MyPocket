package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;
    private User user;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }


    public void activateSubscription(User user) {
        user.setSubscribed(true);
        userDao.update(user);
    }

    public void cancelSubscription(User user) {
        user.setSubscribed(false);
        userDao.update(user);
    }

    public boolean isSubscribed(User user) {
        return user.isSubscribed();
    }
}

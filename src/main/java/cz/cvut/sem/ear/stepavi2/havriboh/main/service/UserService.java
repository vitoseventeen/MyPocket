package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {
    private final UserDao userDao;
    private User user;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }


    public void activateSubscriptionForOneMonth(User user) {
        user.setSubscriptionStartDate(LocalDate.now());
        user.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
        user.setSubscribed(true);
        userDao.update(user);
    }

    public void cancelSubscription(User user) {
        user.setSubscriptionEndDate(LocalDate.now());
        user.setSubscribed(false);
        userDao.update(user);
    }

    public boolean isSubscribed(User user) {
        // if subscription end date is today or later than today = user is not subscribed
        if (user.getSubscriptionEndDate().isAfter(LocalDate.now())) {
            return true;
        } else {
            return false;
        }
    }
}

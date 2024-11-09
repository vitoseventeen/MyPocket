package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.PremiumUser;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Calendar;

@Service
public class PremiumUserService {

    private final UserDao userDao;

    @Autowired
    public PremiumUserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void activateSubscription(PremiumUser user, int months) {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, months);

        // user.setIsSubscribed(true);
        user.setSubscriptionStartDate(currentDate);
        user.setSubscriptionEndDate(calendar.getTime());
        userDao.save(user);
    }

    public void cancelSubscription(PremiumUser user) {
        // user.setIsSubscribed(false);
        user.setSubscriptionStartDate(null);
        user.setSubscriptionEndDate(null);
        userDao.save(user);
    }

    public void checkSubscriptionStatus(PremiumUser user) {
        Date currentDate = new Date();
        if (user.isSubscribed() && user.getSubscriptionEndDate().before(currentDate)) {
            cancelSubscription(user);
        }
    }
}

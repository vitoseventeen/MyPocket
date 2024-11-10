package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.PremiumUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Calendar;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PremiumUserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private PremiumUserService premiumUserService;

    private PremiumUser premiumUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        premiumUser = new PremiumUser();
        premiumUser.setId(1);
    }

    @Test
    void testActivateSubscription() {
        int months = 3;

        premiumUserService.activateSubscription(premiumUser, months);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, months);
        Date expectedEndDate = calendar.getTime();

        assertNotNull(premiumUser.getSubscriptionStartDate());
        assertEquals(expectedEndDate.getTime()/100, premiumUser.getSubscriptionEndDate().getTime()/100);

        verify(userDao, times(1)).save(premiumUser);
    }

    @Test
    void testCancelSubscription() {
        premiumUserService.cancelSubscription(premiumUser);

        assertNull(premiumUser.getSubscriptionStartDate());
        assertNull(premiumUser.getSubscriptionEndDate());

        verify(userDao, times(1)).save(premiumUser);
    }

    @Test
    void testCheckSubscriptionStatus_subscribedAndNotExpired() {
        premiumUser.setSubscriptionStartDate(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 3);
        premiumUser.setSubscriptionEndDate(calendar.getTime());

        premiumUserService.checkSubscriptionStatus(premiumUser);

        verify(userDao, times(0)).save(premiumUser);
    }


    @Test
    void testCheckSubscriptionStatus_notSubscribed() {

        premiumUser.setSubscriptionStartDate(null);
        premiumUser.setSubscriptionEndDate(null);

        premiumUserService.checkSubscriptionStatus(premiumUser);

        verify(userDao, times(0)).save(premiumUser);
    }
}

package cz.cvut.fel.ear.stepavi2_havriboh.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.AccountDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.SubscriptionNotActiveException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;


    // CREATING A PREMIUM USER;
    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setSubscriptionStartDate(LocalDate.now());
        testUser.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
        testUser.setPassword(passwordEncoder.encode("oldPassword"));
        userDao.persist(testUser);
    }


    @Test
    void activateSubscriptionForOneMonthCreatesNewSubscription() {
        User user = new User();
        user.setEmail("userserser@gmail.com");
        user.setUsername("userwqeqwe");
        user.setPassword(passwordEncoder.encode("password"));
        userDao.persist(user);

        userService.activateSubscription(user.getId(), 1);

        User updatedUser = userDao.findById(testUser.getId()).orElseThrow();
        assertTrue(updatedUser.isSubscribed());
        assertEquals(LocalDate.now().plusMonths(1), updatedUser.getSubscriptionEndDate());
    }

    @Test
    void activateSubscriptionForOneMonthUpdatesExistingSubscription() {
        userService.activateSubscription(testUser.getId(), 1);
        LocalDate firstSubscriptionEndDate = testUser.getSubscriptionEndDate();
        userService.activateSubscription(testUser.getId(), 1);
        User updatedUser = userDao.findById(testUser.getId()).orElseThrow();
        assertEquals(firstSubscriptionEndDate.plusMonths(1), updatedUser.getSubscriptionEndDate());
    }

    @Test
    void cancelSubscriptionCancelsSubscription() {
        userService.activateSubscription(testUser.getId(), 1);
        testUser.setSubscriptionEndDate(LocalDate.now().plusMonths(2));
        userDao.update(testUser);

        userService.cancelSubscription(testUser.getId());

        User updatedUser = userDao.findById(testUser.getId()).orElseThrow();
        assertFalse(updatedUser.isSubscribed());
        assertEquals(LocalDate.now(), updatedUser.getSubscriptionEndDate());
    }
    @Test
    void cancelSubscriptionThrowsSubscriptionNotActiveException() {
        userService.cancelSubscription(testUser.getId());
        testUser.setSubscriptionEndDate(LocalDate.now().minusDays(1));
        userDao.update(testUser);

        assertThrows(SubscriptionNotActiveException.class, () ->
                userService.cancelSubscription(testUser.getId())
        );
    }
}

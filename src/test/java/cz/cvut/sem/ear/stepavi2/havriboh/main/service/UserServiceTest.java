package cz.cvut.sem.ear.stepavi2.havriboh.main.service;


import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.EmailAlreadyTakenException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.SubscriptionNotActiveException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.UserNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
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

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setSubscribed(false);
        testUser.setSubscriptionStartDate(LocalDate.now());
        testUser.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
        userDao.persist(testUser);
    }

    @Test
    void activateSubscriptionForOneMonthCreatesNewSubscription() {
        userService.activateSubscriptionForOneMonth(testUser);

        User updatedUser = userDao.findUserById(testUser.getId()).orElseThrow();
        assertTrue(updatedUser.isSubscribed());
        assertEquals(LocalDate.now().plusMonths(1), updatedUser.getSubscriptionEndDate());
    }

    @Test
    void activateSubscriptionForOneMonthUpdatesExistingSubscription() {
        testUser.setSubscribed(true);
        testUser.setSubscriptionEndDate(LocalDate.now().plusMonths(2));
        userDao.update(testUser);

        userService.activateSubscriptionForOneMonth(testUser);

        User updatedUser = userDao.findUserById(testUser.getId()).orElseThrow();
        assertEquals(LocalDate.now().plusMonths(3), updatedUser.getSubscriptionEndDate());
    }
    @Test
    void cancelSubscriptionCancelsSubscription() {
        testUser.setSubscribed(true);
        testUser.setSubscriptionEndDate(LocalDate.now().plusMonths(2));
        userDao.update(testUser);

        userService.cancelSubscription(testUser);

        User updatedUser = userDao.findUserById(testUser.getId()).orElseThrow();
        assertFalse(updatedUser.isSubscribed());
        assertEquals(LocalDate.now(), updatedUser.getSubscriptionEndDate());
    }
    @Test
    void cancelSubscriptionThrowsSubscriptionNotActiveException() {
        testUser.setSubscribed(false);
        testUser.setSubscriptionEndDate(LocalDate.now().minusDays(1));
        userDao.update(testUser);

        assertThrows(SubscriptionNotActiveException.class, () ->
                userService.cancelSubscription(testUser)
        );
    }

    @Test
    void updateUsernameByIdUpdatesUsername() {
        String newUsername = "updateduser";
        userService.updateUsernameById(testUser.getId(), newUsername);

        User updatedUser = userDao.findUserById(testUser.getId()).orElseThrow();
        assertEquals(newUsername, updatedUser.getUsername());
    }

    @Test
    void updateUsernameByIdThrowsUsernameAlreadyTakenException() {
        User anotherUser = new User();
        anotherUser.setEmail("anotheruser@example.com");
        anotherUser.setUsername("anotherusername");
        userDao.persist(anotherUser);

        assertThrows(UsernameAlreadyTakenException.class, () ->
                userService.updateUsernameById(testUser.getId(), "anotherusername")
        );
    }

}

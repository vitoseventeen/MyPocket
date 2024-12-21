package cz.cvut.fel.ear.stepavi2_havriboh.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.SubscriptionNotActiveException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.UsernameAlreadyTakenException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role;
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
    private PasswordEncoder passwordEncoder;

    private User testUser;

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
    void updatePasswordSuccessfullyUpdatesPassword() {
        String newPassword = "newPassword123";

        userService.updatePassword(testUser.getId(), newPassword);

        User updatedUser = userDao.findById(testUser.getId()).orElseThrow();

        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    void deleteUserByUsernameDeletesUser() {
        userService.deleteUserByUsername(testUser.getUsername());

        assertFalse(userDao.findByUsername(testUser.getUsername()).isPresent());
    }

    @Test
    void activateSubscriptionForOneMonthCreatesNewSubscription() {
        User user = new User();
        user.setEmail("userserser@gmail.com");
        user.setUsername("userwqeqwe");
        user.setPassword(passwordEncoder.encode("password"));
        userService.activateSubscription(user, 1);

        User updatedUser = userDao.findById(testUser.getId()).orElseThrow();
        assertTrue(updatedUser.isSubscribed());
        assertEquals(LocalDate.now().plusMonths(1), updatedUser.getSubscriptionEndDate());
    }

    @Test
    void activateSubscriptionForOneMonthUpdatesExistingSubscription() {
        userService.activateSubscription(testUser, 1);
        testUser.setSubscriptionEndDate(LocalDate.now().plusMonths(2));
        userDao.update(testUser);

        userService.activateSubscription(testUser,1);

        User updatedUser = userDao.findById(testUser.getId()).orElseThrow();
        assertEquals(LocalDate.now().plusMonths(3), updatedUser.getSubscriptionEndDate());
    }
    @Test
    void cancelSubscriptionCancelsSubscription() {
        userService.activateSubscription(testUser, 1);
        testUser.setSubscriptionEndDate(LocalDate.now().plusMonths(2));
        userDao.update(testUser);

        userService.cancelSubscription(testUser);

        User updatedUser = userDao.findById(testUser.getId()).orElseThrow();
        assertFalse(updatedUser.isSubscribed());
        assertEquals(LocalDate.now(), updatedUser.getSubscriptionEndDate());
    }
    @Test
    void cancelSubscriptionThrowsSubscriptionNotActiveException() {
        userService.cancelSubscription(testUser);
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

        User updatedUser = userDao.findById(testUser.getId()).orElseThrow();
        assertEquals(newUsername, updatedUser.getUsername());
    }

    @Test
    void updateUsernameByIdThrowsUsernameAlreadyTakenException() {
        User otherUser = new User();
        otherUser.setEmail("testmailll@gmail.com");
        otherUser.setUsername("otherUser");
        userService.cancelSubscription(testUser);
        otherUser.setPassword(passwordEncoder.encode("password"));
        otherUser.setRole(Role.USER);
        userDao.persist(otherUser);

        assertThrows(UsernameAlreadyTakenException.class, () ->
                userService.updateUsernameById(testUser.getId(), otherUser.getUsername())
        );
    }

    @Test
    void isSubscribedReturnsTrueIfUserIsSubscribed() {
        userService.activateSubscription(testUser, 1);
        testUser.setSubscriptionEndDate(LocalDate.now().plusDays(1));
        userDao.update(testUser);

        assertTrue(userService.isSubscribed(testUser));
    }
}

package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.EmailAlreadyTakenException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.SubscriptionNotActiveException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.UserNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.UsernameAlreadyTakenException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDao dao, PasswordEncoder passwordEncoder) {
        this.userDao = dao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUser(String email, String username, String password) {
        if (userDao.findByEmail(email).isPresent()) {
            throw new EmailAlreadyTakenException("This email is already taken.");
        }
        if (userDao.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyTakenException("This username is already taken.");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setSubscribed(false);

        // Encode the password before saving it
        user.setPassword(passwordEncoder.encode(password));

        userDao.persist(user);
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userDao.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public User getUserById(int id) {
        return userDao.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    @Transactional
    public void deleteUserById(int userId) {
        User user = getUserById(userId);
        userDao.remove(user);
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        User user = getUserByEmail(email);
        userDao.remove(user);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        User user = getUserByUsername(username);
        userDao.remove(user);
    }

    @Transactional
    public void updateUsernameById(int userId, String newUsername) {
        User user = getUserById(userId);
        if (userDao.findByUsername(newUsername).isPresent()) {
            throw new UsernameAlreadyTakenException("This username is already taken.");
        }
        user.setUsername(newUsername);
        userDao.update(user);
    }

    @Transactional
    public void updateEmailById(int userId, String newEmail) {
        User user = getUserById(userId);
        if (userDao.findByEmail(newEmail).isPresent()) {
            throw new EmailAlreadyTakenException("This email is already taken.");
        }
        user.setEmail(newEmail);
        userDao.update(user);
    }

    @Transactional
    public void activateSubscriptionForOneMonth(User user) {
        if (user.isSubscribed() && user.getSubscriptionEndDate().isAfter(LocalDate.now())) {
            // if subscription is active and not expired, extend it by one month
            user.setSubscriptionEndDate(user.getSubscriptionEndDate().plusMonths(1));
        } else {
            // if subscription is expired or not active, create new subscription
            user.setSubscriptionStartDate(LocalDate.now());
            user.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
            user.setSubscribed(true);
        }
        userDao.update(user);
    }

    @Transactional
    public void cancelSubscription(User user) {
        if (!user.isSubscribed()) {
            throw new SubscriptionNotActiveException("User is not subscribed");
        }
        user.setSubscriptionEndDate(LocalDate.now());
        user.setSubscribed(false);
        userDao.update(user);
    }

    @Transactional(readOnly = true)
    public boolean isSubscribed(User user) {
        return user.isSubscribed() && !user.getSubscriptionEndDate().isBefore(LocalDate.now());
    }

    // Method to update the password
    @Transactional
    public void updatePassword(int userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword)); // Encode new password
        userDao.update(user);
    }

    // Method to check if the provided password matches the stored (encoded) password
    @Transactional(readOnly = true)
    public boolean checkPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }
}

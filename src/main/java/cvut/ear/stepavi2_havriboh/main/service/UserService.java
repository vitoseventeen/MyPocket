package cvut.ear.stepavi2_havriboh.main.service;

import cvut.ear.stepavi2_havriboh.main.dao.UserDao;
import cvut.ear.stepavi2_havriboh.main.exception.*;
import cvut.ear.stepavi2_havriboh.main.model.Role;
import cvut.ear.stepavi2_havriboh.main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    public void createUser(String username, String email, String password) {
        if (userDao.findByEmail(email).isPresent()) {
            throw new EmailAlreadyTakenException("This email is already taken.");
        }
        if (userDao.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyTakenException("This username is already taken.");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setRole(Role.USER);

        user.setPassword(passwordEncoder.encode(password));

        userDao.persist(user);
    }


    @Transactional(readOnly = true)
    public boolean exists(String username) {
        return userDao.findByUsername(username).isPresent();
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
    public void activateSubscription(User user, int months) {
        if (months <= 0) {
            throw new InvalidDataException("Subscription period must be a positive number of months.");
        }

        if (user.isSubscribed() && user.getSubscriptionEndDate().isAfter(LocalDate.now())) {
            user.setSubscriptionEndDate(user.getSubscriptionEndDate().plusMonths(months));
        } else {
            user.setSubscriptionStartDate(LocalDate.now());
            user.setSubscriptionEndDate(LocalDate.now().plusMonths(months));
        }

        if (user.isSubscribed()) {
            user.setRole(Role.PREMIUM);
        }
        userDao.update(user);
    }


    @Transactional
    public void cancelSubscription(User user) {
        if (!user.isSubscribed()) {
            throw new SubscriptionNotActiveException("User is not subscribed");
        }
        user.setSubscriptionEndDate(LocalDate.now());
        user.setRole(Role.USER);
        userDao.update(user);
    }

    @Transactional(readOnly = true)
    public boolean isSubscribed(User user) {
        return user.isSubscribed() && !user.getSubscriptionEndDate().isBefore(LocalDate.now());
    }

    // Method to update the password
    // pridat kontrolu autorizace
    @Transactional
    public void updatePassword(int userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword)); // Encode new password
        userDao.update(user);
    }

    // pridat kontrolu
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Transactional
    public void updateUser(User existingUser) {
        userDao.update(existingUser);
    }
}

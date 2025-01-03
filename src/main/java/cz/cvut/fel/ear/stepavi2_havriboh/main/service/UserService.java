package cz.cvut.fel.ear.stepavi2_havriboh.main.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
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
    public User getUserById(int id) {
        return userDao.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    @Transactional
    public void deleteUserById(int userId) {
        User user = getUserById(userId);
        userDao.remove(user);
    }

    @Transactional
    public void activateSubscription(int userId, int months) {
        User user = getUserById(userId);

        if (months <= 0) {
            throw new InvalidDataException("Subscription period must be a positive number of months.");
        }

        if (user.isSubscribed() && user.getSubscriptionEndDate().isAfter(LocalDate.now())) {
            user.setSubscriptionEndDate(user.getSubscriptionEndDate().plusMonths(months));
        } else {
            user.setSubscriptionStartDate(LocalDate.now());
            user.setSubscriptionEndDate(LocalDate.now().plusMonths(months));
        }

        user.setRole(Role.PREMIUM);
        userDao.update(user);
    }

    @Transactional
    public void cancelSubscription(int userId) {
        User user = getUserById(userId);
        if (!user.isSubscribed()) {
            throw new SubscriptionNotActiveException("User is not subscribed");
        }
        user.setSubscriptionEndDate(LocalDate.now());
        user.setRole(Role.USER);
        userDao.update(user);
    }

    @Transactional
    public void updateUser(User existingUser, User newUser) {
        if (newUser.getUsername() != null && !newUser.getUsername().equals(existingUser.getUsername())) {
            if (userDao.findByUsername(newUser.getUsername()).isPresent()) {
                throw new UsernameAlreadyTakenException("This username is already taken.");
            }
            existingUser.setUsername(newUser.getUsername());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().equals(existingUser.getEmail())) {
            if (userDao.findByEmail(newUser.getEmail()).isPresent()) {
                throw new EmailAlreadyTakenException("This email is already taken.");
            }
            existingUser.setEmail(newUser.getEmail());
        }

        if (newUser.getPassword() != null && !newUser.getPassword().equals(existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }

        userDao.update(existingUser);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Transactional
    public void updateUser(User existingUser) {
        userDao.update(existingUser);
    }
}

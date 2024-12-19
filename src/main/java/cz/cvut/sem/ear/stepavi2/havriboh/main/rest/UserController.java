package cz.cvut.sem.ear.stepavi2.havriboh.main.rest;

import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.SubscriptionNotActiveException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.UserNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import cz.cvut.sem.ear.stepavi2.havriboh.main.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        logger.info("Fetching all users");
        try {
            return ResponseEntity.ok().body(userService.findAll());
        } catch (Exception e) {
            logger.error("Error getting users: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error getting users: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") int id) {
        logger.info("Fetching user with ID: {}", id);
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok().body(user);
        } catch (UserNotFoundException e) {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        logger.info("Creating user: {}", user.getUsername());
        try {
            userService.createUser(user.getUsername(), user.getEmail(), user.getPassword());
            return ResponseEntity.status(201).body("User created successfully");
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error creating user: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") int id, @RequestBody User user) {
        logger.info("Updating user with ID: {}", id);
        try {
            User existingUser = userService.getUserById(id);
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            userService.updateUser(existingUser);
            return ResponseEntity.ok("User with ID " + id + " updated successfully");
        } catch (UserNotFoundException e) {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") int id) {
        logger.info("Deleting user with ID: {}", id);
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body("User with ID " + id + " deleted successfully");
        } catch (UserNotFoundException e) {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}: {}", id, e.getMessage());
            return ResponseEntity.status(400).body("Error deleting user: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/activate-subscription")
    public ResponseEntity<Object> activateSubscription(@PathVariable("id") int id, @RequestParam(value = "months", defaultValue = "1") int months) {
        logger.info("Activating subscription for user with ID: {} for {} month(s)", id, months);
        try {
            User user = userService.getUserById(id);
            userService.activateSubscription(user, months);
            return ResponseEntity.ok().body("Subscription activated for user with ID: " + id + " for " + months + " month(s)");
        } catch (UserNotFoundException e) {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        }
    }

    @PostMapping("/{id}/cancel-subscription")
    public ResponseEntity<Object> cancelSubscription(@PathVariable("id") int id) {
        logger.info("Cancelling subscription for user with ID: {}", id);
        try {
            User user = userService.getUserById(id);
            userService.cancelSubscription(user);
            return ResponseEntity.ok().body("Subscription cancelled for user with ID: " + id);
        } catch (UserNotFoundException e) {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        } catch (SubscriptionNotActiveException e) {
            logger.warn("Subscription not active for user with ID: {}", id);
            return ResponseEntity.status(400).body("Subscription is not active for user with ID: " + id);
        }
    }
}

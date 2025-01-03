package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.SubscriptionNotActiveException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.UserNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/rest/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    private boolean checkUserPerms(int userId) {
        User currentUser = Objects.requireNonNull(SecurityUtils.getCurrentUser(), "Current user cannot be null.");

        boolean isOwner = currentUser.getId().equals(userId);
        boolean isAdmin = currentUser.isAdmin();

        return !isOwner && !isAdmin;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") int id) {
        logger.info("Fetching user with ID: {}", id);
        try {
            if (checkUserPerms(id)) {
                throw new AccessDeniedException("Access denied");
            }
            User user = userService.getUserById(id);
            return ResponseEntity.ok().body(user);
        } catch (UserNotFoundException e) {
            logger.error("User not found with ID: {}", id);
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_PREMIUM')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") int id, @RequestBody User user) {
        logger.info("Updating user with ID: {}", id);
        try {
            if (checkUserPerms(id)) {
                throw new AccessDeniedException("Access denied");
            }
            User existingUser = userService.getUserById(id);
            userService.updateUser(existingUser, user);
            return ResponseEntity.ok("User with ID " + id + " updated successfully");
        } catch (UserNotFoundException e) {
            logger.error("User not found with ID: {}", id);
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}: {}", id, e.getMessage());
            return ResponseEntity.status(400).body("Error updating user: " + e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") int id) {
        logger.info("Deleting user with ID: {}", id);
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body("User with ID " + id + " deleted successfully");
        } catch (UserNotFoundException e) {
            logger.error("User not found with ID: {}", id);
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}: {}", id, e.getMessage());
            return ResponseEntity.status(400).body("Error deleting user: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_PREMIUM')")
    @PostMapping("/{id}/activate-subscription")
    public ResponseEntity<Object> activateSubscription(@PathVariable("id") int id, @RequestBody Map<String, Integer> requestBody) {
        int months = requestBody.getOrDefault("months", 1);
        logger.info("Activating subscription for user with ID: {} for {} month(s)", id, months);
        try {
            userService.activateSubscription(id, months);
            return ResponseEntity.ok().body("Subscription activated for user with ID: " + id + " for " + months + " month(s)");
        } catch (UserNotFoundException e) {
            logger.error("User not found with ID: {}", id);
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_PREMIUM')")
    @PostMapping("/{id}/cancel-subscription")
    public ResponseEntity<Object> cancelSubscription(@PathVariable("id") int id) {
        logger.info("Cancelling subscription for user with ID: {}", id);
        try {
            if (checkUserPerms(id)) {
                throw new AccessDeniedException("Access denied");
            }
            userService.cancelSubscription(id);
            return ResponseEntity.ok().body("Subscription cancelled for user with ID: " + id);
        } catch (UserNotFoundException e) {
            logger.error("User not found with ID: {}", id);
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        } catch (SubscriptionNotActiveException e) {
            logger.error("Subscription not active for user with ID: {}", id);
            return ResponseEntity.status(400).body("Subscription is not active for user with ID: " + id);
        }
    }
}

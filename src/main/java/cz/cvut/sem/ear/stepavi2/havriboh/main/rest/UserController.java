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
        return ResponseEntity.ok().body("List of all users");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") int id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok().body(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        try {
            userService.createUser(user.getUsername(), user.getEmail(), user.getPassword());
            return ResponseEntity.status(201).body("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating user: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") int id, @RequestBody User user) {
        try {
            userService.updateUsernameById(id, user.getUsername());
            userService.updateEmailById(id, user.getEmail());
            return ResponseEntity.ok().body("User with ID " + id + " updated successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error updating user: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") int id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body("User with ID " + id + " deleted successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error deleting user: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/activate-subscription")
    public ResponseEntity<Object> activateSubscription(@PathVariable("id") int id) {
        try {
            User user = userService.getUserById(id);
            userService.activateSubscriptionForOneMonth(user);
            return ResponseEntity.ok().body("Subscription activated for user with ID: " + id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error activating subscription: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel-subscription")
    public ResponseEntity<Object> cancelSubscription(@PathVariable("id") int id) {
        try {
            User user = userService.getUserById(id);
            userService.cancelSubscription(user);
            return ResponseEntity.ok().body("Subscription cancelled for user with ID: " + id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("User not found with ID: " + id);
        } catch (SubscriptionNotActiveException e) {
            return ResponseEntity.status(400).body("Subscription is not active for user with ID: " + id);
        }
    }
}

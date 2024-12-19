package cz.cvut.sem.ear.stepavi2.havriboh.main.rest;

import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.SubscriptionNotActiveException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.UserNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import cz.cvut.sem.ear.stepavi2.havriboh.main.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends BaseControllerTestRunner {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);
        super.setUp(userController);
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(new User(), new User())); // Mocking findAll()

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        when(userService.getUserById(anyInt())).thenReturn(user); // Mocking getUserById

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    void testGetUserById_UserNotFound() throws Exception {
        when(userService.getUserById(anyInt())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/users/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"User not found with ID: 999\""));
    }

    @Test
    void testCreateUser_Success() throws Exception {
        User user = new User();
        user.setUsername("newUser");
        user.setEmail("newUser@example.com");
        user.setPassword("password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newUser\", \"email\":\"newUser@example.com\", \"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("\"User created successfully\""));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        User user = new User();
        user.setUsername("updatedUser");
        user.setEmail("updatedUser@example.com");

        when(userService.getUserById(anyInt())).thenReturn(user);
        doNothing().when(userService).updateUsernameById(anyInt(), any());
        doNothing().when(userService).updateEmailById(anyInt(), any());

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updatedUser\", \"email\":\"updatedUser@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"User with ID 1 updated successfully\""));
    }


    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUserById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("\"User with ID 1 deleted successfully\""));
    }

    @Test
    void testDeleteUser_UserNotFound() throws Exception {
        doThrow(UserNotFoundException.class).when(userService).deleteUserById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/users/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"User not found with ID: 999\""));
    }

    @Test
    void testActivateSubscription_Success() throws Exception {
        User user = new User();
        user.setUsername("userWithSubscription");

        when(userService.getUserById(anyInt())).thenReturn(user); // Mocking getUserById
        doNothing().when(userService).activateSubscription(user, 1); // Mocking activateSubscription

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/users/{id}/activate-subscription", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Subscription activated for user with ID: 1 for 1 month(s)\""));
    }

    @Test
    void testActivateSubscription_UserNotFound() throws Exception {
        when(userService.getUserById(anyInt())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/users/{id}/activate-subscription", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"User not found with ID: 999\""));
    }

    @Test
    void testCancelSubscription_Success() throws Exception {
        User user = new User();
        user.setUsername("userWithSubscription");

        when(userService.getUserById(anyInt())).thenReturn(user);
        doNothing().when(userService).cancelSubscription(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/users/{id}/cancel-subscription", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Subscription cancelled for user with ID: 1\""));
    }

    @Test
    void testCancelSubscription_UserNotFound() throws Exception {
        when(userService.getUserById(anyInt())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/users/{id}/cancel-subscription", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"User not found with ID: 999\""));
    }

    @Test
    void testCancelSubscription_NotActive() throws Exception {
        User user = new User();
        user.setUsername("userWithInactiveSubscription");
        user.setSubscriptionEndDate(LocalDate.now());

        when(userService.getUserById(anyInt())).thenReturn(user);
        doThrow(SubscriptionNotActiveException.class).when(userService).cancelSubscription(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/users/{id}/cancel-subscription", 1))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("\"Subscription is not active for user with ID: 1\""));
    }

    @Test
    void testUpdateUser_UserNotFound() throws Exception {
        when(userService.getUserById(anyInt())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/users/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updatedUser\", \"email\":\"updatedUser@example.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"User not found with ID: 999\""));
    }


}

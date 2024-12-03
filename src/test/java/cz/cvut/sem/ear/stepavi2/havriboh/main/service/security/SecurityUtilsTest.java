package cz.cvut.sem.ear.stepavi2.havriboh.main.service.security;

import cz.cvut.sem.ear.stepavi2.havriboh.main.environment.Environment;
import cz.cvut.sem.ear.stepavi2.havriboh.main.environment.Generator;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import cz.cvut.sem.ear.stepavi2.havriboh.main.security.SecurityUtils;
import cz.cvut.sem.ear.stepavi2.havriboh.main.security.model.UserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityUtilsTest {

    private User user;

    @BeforeEach
    public void setUp() {
        this.user = Generator.generateUser();
        user.setId(Generator.randomInt());
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getCurrentUserReturnsCurrentlyLoggedInUser() {
        Environment.setCurrentUser(user);
        final User result = SecurityUtils.getCurrentUser();
        assertEquals(user, result);
    }

    @Test
    public void getCurrentUserDetailsReturnsUserDetailsOfCurrentlyLoggedInUser() {
        Environment.setCurrentUser(user);
        final UserDetails result = SecurityUtils.getCurrentUserDetails();
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertEquals(user, result.getUser());
    }

    @Test
    public void getCurrentUserDetailsReturnsNullIfNoUserIsLoggedIn() {
        assertNull(SecurityUtils.getCurrentUserDetails());
    }
}

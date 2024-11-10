package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserDaoTest {
    @Autowired
    private UserDao userDao;

    private User testUser;

    @BeforeEach
    public void setUp() {

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setRole("USER");
        testUser.setSubscribed(true);
    }

    @Test
    public void testSaveUser() {

        userDao.save(testUser);


        User savedUser = userDao.findUserByEmail(testUser.getEmail());
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(savedUser.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(savedUser.getRole()).isEqualTo(testUser.getRole());
        assertThat(savedUser.isSubscribed()).isTrue();
    }

    @Test
    public void testFindUserByEmail_UserExists() {

        userDao.save(testUser);


        User foundUser = userDao.findUserByEmail("test@example.com");


        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(foundUser.getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    public void testFindUserByEmail_UserNotFound() {

        assertThrows(EmptyResultDataAccessException.class, () -> {
            userDao.findUserByEmail("nonexistent@example.com");
        });
    }
}

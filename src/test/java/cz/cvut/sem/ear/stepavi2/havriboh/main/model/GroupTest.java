package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupTest {

    private Group group;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setName("Test Group");

        user1 = new User();
        user1.setId(1);
        user1.setUsername("Alice");

        user2 = new User();
        user2.setId(2);
        user2.setUsername("Bob");

        group.setMembers(new ArrayList<>());
    }

    @Test
    void testAddMember_validMember() {

        group.addMember(user1);


        assertTrue(group.getMembers().contains(user1), "User1 should be in the group");
    }

    @Test
    void testAddMember_duplicateMember() {
        group.addMember(user1);
        group.addMember(user1);

        assertEquals(1, group.getMembers().size(), "User1 should only appear once in the group");
    }

    @Test
    void testAddMember_nullMember() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            group.addMember(null);
        });

        assertEquals("Member cannot be null", exception.getMessage());
    }

    @Test
    void testRemoveMember_validMember() {
        group.addMember(user1);
        group.addMember(user2);

        group.removeMember(user1);

        assertFalse(group.getMembers().contains(user1), "User1 should be removed from the group");
    }

    @Test
    void testRemoveMember_memberNotFound() {
        group.removeMember(user1);

        assertFalse(group.getMembers().contains(user1), "User1 should not be in the group");
    }

}

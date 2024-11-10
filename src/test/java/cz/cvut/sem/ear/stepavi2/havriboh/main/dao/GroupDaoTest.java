package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;


import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Group;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class GroupDaoTest {

    @Autowired
    private GroupDao groupDao;

    @PersistenceContext
    private EntityManager em;

    private Group testGroup;

    @BeforeEach
    void setUp() {
        testGroup = new Group();
        testGroup.setName("TestGroup");
        groupDao.save(testGroup);
        em.flush();
    }

    @Test
    void testSave() {
        Group group = new Group();
        group.setName("NewGroup");

        groupDao.save(group);
        em.flush(); // Ensure the data is saved immediately

        Group foundGroup = em.find(Group.class, group.getId());
        assertNotNull(foundGroup);
        assertEquals("NewGroup", foundGroup.getName());
    }

    @Test
    void testFind() {
        Group foundGroup = groupDao.find(testGroup.getId());
        assertNotNull(foundGroup);
        assertEquals("TestGroup", foundGroup.getName());
    }

    @Test
    void testUpdate() {
        testGroup.setName("UpdatedName");
        groupDao.update(testGroup);
        em.flush();

        Group updatedGroup = em.find(Group.class, testGroup.getId());
        assertNotNull(updatedGroup);
        assertEquals("UpdatedName", updatedGroup.getName());
    }

    @Test
    void testDelete() {
        groupDao.delete(testGroup);
        em.flush();

        Group deletedGroup = em.find(Group.class, testGroup.getId());
        assertNull(deletedGroup);
    }

    @Test
    void testFindByName() {
        Group foundGroup = groupDao.findByName("TestGroup");
        assertNotNull(foundGroup);
        assertEquals("TestGroup", foundGroup.getName());
    }

}

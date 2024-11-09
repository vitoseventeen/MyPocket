package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Group;
import org.springframework.stereotype.Repository;

@Repository
public class GroupDao extends BaseDao<Group> {
    public GroupDao() {
        super(Group.class);
    }

    public void save(Group group) {
        em.persist(group);
    }
}

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

    public Group update(Group group) {
        em.merge(group);
        return group;
    }

    public void delete(Group group) {
        em.remove(group);
    }

    public Group find(int id) {
        return em.find(Group.class, id);
    }

    public Group findByName(String name) {
        return em.createQuery("SELECT g FROM Group g WHERE g.name = :name", Group.class)
                .setParameter("name", name)
                .getSingleResult();
    }


}

package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.GroupDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.NotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.UserAlreadyInGroupException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Group;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    private final GroupDao groupDao;

    @Autowired
    public GroupService(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public void addUserToGroup(User user, Group group) {
        if (group.getMembers().contains(user)) {
            throw new UserAlreadyInGroupException("User is already in group");
        }
        group.getMembers().add(user);
        groupDao.save(group);
    }

    public void removeUserFromGroup(User user, Group group) {
        if (!group.getMembers().contains(user)) {
            throw new NotFoundException("User is not found in group");
        }
        group.getMembers().remove(user);
        groupDao.save(group);
    }

    public void createGroup(Group group) {
        groupDao.persist(group);
    }

    public void deleteGroup(Group group) {
        groupDao.remove(group);
    }
}

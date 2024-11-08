package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class Group extends AbstractEntity {
    private String name;
    private List<User> members;

    public void addMember(User newMember) {}
    public void removeMember(User member) {}
}

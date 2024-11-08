package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Group extends AbstractEntity {
    private String name;

    @ManyToMany
    private List<User> members;

    public void addMember(User newMember) {}
    public void removeMember(User member) {}

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", members=" + members +
                '}';
    }
}

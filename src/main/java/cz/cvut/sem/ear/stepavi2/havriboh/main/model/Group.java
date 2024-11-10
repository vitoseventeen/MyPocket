package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group extends AbstractEntity {
    private String name;

    @ManyToMany
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;

    public void addMember(User newMember) {
        if (newMember == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        if (!members.contains(newMember)) {
            members.add(newMember);
        } else {
            System.out.println("Member is already in the group");
        }
    }
    public void removeMember(User member) {
        if (!members.contains(member)) {
            System.out.println("Member not found in the group");
        } else {
            members.remove(member);
        }
    }

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return new ArrayList<>(members);
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

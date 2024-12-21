package cz.cvut.fel.ear.stepavi2_havriboh.main.model;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "accounts")
public class Account extends AbstractEntity {

    @Column(name = "account_name", nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "account", cascade = CascadeType.MERGE, orphanRemoval = true)
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "account_user",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    @OneToOne(mappedBy = "account", cascade = CascadeType.MERGE, orphanRemoval = true)
    @JsonIgnore
    private Budget budget;



    @JsonGetter("members")
    public List<String> getMemberUsernames() {
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            usernames.add(user.getUsername());
        }
        return usernames;
    }



    public Account() {
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountName='" + name + '\'' +
                ", transactions=" + transactions +
                ", users=" + users +
                ", budget=" + budget +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }
}

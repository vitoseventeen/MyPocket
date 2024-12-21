package cz.cvut.fel.ear.stepavi2_havriboh.main.model;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.ear.stepavi2_havriboh.main.utils.CurrencyConverter;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "accounts")
public class Account extends AbstractEntity {

    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @OneToMany(mappedBy = "account", cascade = CascadeType.MERGE, orphanRemoval = true)
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "account_user",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

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
                "accountName='" + accountName + '\'' +
                ", transactions=" + transactions +
                ", users=" + users +
                ", budget=" + budget +
                '}';
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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

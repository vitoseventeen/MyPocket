package cz.cvut.fel.ear.stepavi2_havriboh.main.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@NamedQueries({
        @NamedQuery(
                name = "Account.findById",
                query = "SELECT a FROM Account a WHERE a.id = :id"
        ),
        @NamedQuery(
                name = "Account.findByName",
                query = "SELECT a FROM Account a WHERE a.name = :name"
        ),
        @NamedQuery(
                name = "Account.findByCreatorId",
                query = "SELECT a FROM Account a WHERE a.creator.id = :creatorId"
        )
})
public class Account extends AbstractEntity {

    @Column(name = "account_name", nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonIgnore
    @OrderBy("date DESC")
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fromDate DESC")
    private List<Report> reports;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "account_user",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    @JsonGetter("members")
    public List<String> getMemberUsernames() {
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            usernames.add(user.getUsername());
        }
        return usernames;
    }

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("budget")
    private Budget budget;


    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @JsonGetter("creator")
    public String getCreatorUsername() {
        return creator != null ? creator.getUsername() : null;
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
                ", creator=" + (creator != null ? creator.getUsername() : "null") +
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public boolean isCreatedBy(User user) {
        return creator != null && creator.equals(user);
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}

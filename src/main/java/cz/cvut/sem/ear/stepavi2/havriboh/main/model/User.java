package cz.cvut.sem.ear.stepavi2.havriboh.main.model;


import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USER_TYPE")
@DiscriminatorValue("USER")
@Table(name = "users")
public class User extends AbstractEntity {

    protected String role;
    protected String email;
    protected String username;
    protected boolean isSubscribed;
    protected Date subscriptionStartDate;
    protected Date subscriptionEndDate;
    @OneToMany(mappedBy = "user")
    private List<Report> reports;

    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;



    @ManyToMany(mappedBy = "user")
    private List<Account> accounts;


    public void cancelSubscription() {
    }

    public void activateSubscription(Date startDate, Date endDate) {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public Date getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public void setSubscriptionStartDate(Date subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public Date getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", isSubscribed=" + isSubscribed +
                ", subscriptionStartDate=" + subscriptionStartDate +
                ", subscriptionEndDate=" + subscriptionEndDate +
                '}';
    }
}

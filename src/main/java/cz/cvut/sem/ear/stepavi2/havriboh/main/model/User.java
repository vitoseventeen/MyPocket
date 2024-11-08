package cz.cvut.sem.ear.stepavi2.havriboh.main.model;


import jakarta.persistence.Entity;

import java.util.Date;

@Entity
public abstract class User extends AbstractEntity {

    private String role;
    private String email;
    private String username;
    private boolean isSubscribed;
    private Date subscriptionStartDate;
    private Date subscriptionEndDate;

    public void cancelSubscription() {
    }

    public void activateSubscription(Date startDate, Date endDate) {
    }
}

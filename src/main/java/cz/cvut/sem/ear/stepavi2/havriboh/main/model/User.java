package cz.cvut.sem.ear.stepavi2.havriboh.main.model;


import jakarta.persistence.Entity;

import java.util.Date;

@Entity
public class User extends AbstractEntity {

    protected String role;
    protected String email;
    protected String username;
    protected boolean isSubscribed;
    protected Date subscriptionStartDate;
    protected Date subscriptionEndDate;

    public void cancelSubscription() {
    }

    public void activateSubscription(Date startDate, Date endDate) {
    }
}

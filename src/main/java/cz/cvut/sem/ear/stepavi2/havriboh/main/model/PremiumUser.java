package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import jakarta.persistence.Basic;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Date;

@Entity
@DiscriminatorValue("PREMIUM_USER")
public class PremiumUser extends User {

    public Report generateReport(String reportType, Date dateFrom, Date dateTo) {
        return new Report();
    }
}

package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;

import java.util.Date;

@Entity
public class PremiumUser extends User {

    public Report generateReport(String reportType, Date dateFrom, Date dateTo) {
        return new Report();
    }
}

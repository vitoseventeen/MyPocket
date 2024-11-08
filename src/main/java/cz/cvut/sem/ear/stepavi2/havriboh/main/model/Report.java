package cz.cvut.sem.ear.stepavi2.havriboh.main.model;


import jakarta.persistence.Entity;

import java.util.Date;

@Entity
public class Report extends AbstractEntity {
    private String reportType;
    private Date fromDate;
    private Date toDate;
}

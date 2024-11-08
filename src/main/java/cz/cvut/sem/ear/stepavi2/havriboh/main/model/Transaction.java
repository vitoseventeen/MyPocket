package cz.cvut.sem.ear.stepavi2.havriboh.main.model;


import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Transaction extends AbstractEntity {
    private BigDecimal amount;
    private Date date;
    private String description;
    private String type;

    public void addTransaction(BigDecimal amount, Date date, String description, String type) {
    }

    //TODO implement
    public void deleteTransaction() {
    }
}

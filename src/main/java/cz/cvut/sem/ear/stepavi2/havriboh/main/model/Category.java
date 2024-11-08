package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class Category extends AbstractEntity {

    private String name;
    private String description;
    private BigDecimal defaultLimit;

    public void setLimit() {}
    public void getTransactions() {}
}

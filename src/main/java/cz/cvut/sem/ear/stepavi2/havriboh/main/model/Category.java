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


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getDefaultLimit() {
        return defaultLimit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDefaultLimit(BigDecimal defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", defaultLimit=" + defaultLimit +
                '}';
    }
}

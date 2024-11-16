package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Category extends AbstractEntity {

    private String name;
    private String description;
    private BigDecimal defaultLimit;

    @OneToMany(mappedBy = "category")
    private List<Transaction> transactions;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    public Category(String name, BigDecimal defaultLimit) {
        this.name = name;
        this.defaultLimit = defaultLimit;
    }

    public Category() {

    }

    public void setLimit(BigDecimal limit) {
        this.defaultLimit = limit;
    }

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

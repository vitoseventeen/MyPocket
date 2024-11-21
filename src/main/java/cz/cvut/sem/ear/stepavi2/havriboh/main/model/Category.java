package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.NegativeCategoryLimitException;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class Category extends AbstractEntity {

    private String name;
    private String description;
    private BigDecimal defaultLimit;

    @OneToOne(mappedBy = "category")
    private Budget budget;

    @OneToMany(mappedBy = "category")
    private List<Transaction> transactions;

    public Category() {}

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


    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }


    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
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

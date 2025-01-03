package cz.cvut.fel.ear.stepavi2_havriboh.main.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@JsonPropertyOrder({"id","amount", "currency", "date", "description", "type", "account_id"})
@NamedQueries({
        @NamedQuery(name = "Transaction.findByAccount", query = "SELECT t FROM Transaction t WHERE t.account.id = :accountId"),
        @NamedQuery(name = "Transaction.findByDateRange", query = "SELECT t FROM Transaction t WHERE t.date BETWEEN :startDate AND :endDate"),
        @NamedQuery(name = "Transaction.findByCategory", query = "SELECT t FROM Transaction t WHERE t.category.id = :categoryId"),
        @NamedQuery(name = "Transaction.findByBudget", query = "SELECT t FROM Transaction t WHERE t.budget.id = :budgetId")
})
public class Transaction extends AbstractEntity {

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @JsonProperty("currency")
    public String getCurrency() {
        return budget != null ? budget.getCurrency().toString() : null;
    }


    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    @JsonProperty("account_id")
    public Integer getAccountId() {
        return account != null ? account.getId() : null;
    }

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "budget_id")
    @JsonIgnore
    private Budget budget;

    public Transaction() {
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", account=" + account +
                ", category=" + category +
                ", budget=" + budget +
                '}';
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    @JsonIgnore
    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }
}

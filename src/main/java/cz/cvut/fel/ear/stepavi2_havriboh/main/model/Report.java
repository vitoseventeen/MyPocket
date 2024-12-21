package cz.cvut.fel.ear.stepavi2_havriboh.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "reports")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report extends AbstractEntity {

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @ManyToMany(mappedBy = "reports")
    private List<Transaction> transactions;


    @Transient
    private Map<String, BigDecimal> incomeByCategory;

    @Transient
    private Map<String, BigDecimal> spendingByCategory;

    @Override
    public String toString() {
        return "Report{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", transactions=" + transactions +
                ", incomeByCategory=" + incomeByCategory +
                ", spendingByCategory=" + spendingByCategory +
                '}';
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Map<String, BigDecimal> getIncomeByCategory() {
        return incomeByCategory;
    }

    public void setIncomeByCategory(Map<String, BigDecimal> incomeByCategory) {
        this.incomeByCategory = incomeByCategory;
    }

    public Map<String, BigDecimal> getSpendingByCategory() {
        return spendingByCategory;
    }

    public void setSpendingByCategory(Map<String, BigDecimal> spendingByCategory) {
        this.spendingByCategory = spendingByCategory;
    }

    public Account getAccount() {
        return transactions.get(0).getAccount();
    }

    public void setAccount(Account account) {
        transactions.forEach(t -> t.setAccount(account));
    }

}

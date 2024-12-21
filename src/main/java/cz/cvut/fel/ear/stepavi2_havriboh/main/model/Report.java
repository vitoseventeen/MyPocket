package cz.cvut.fel.ear.stepavi2_havriboh.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "reports")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report extends AbstractEntity {

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Чтобы избежать бесконечных циклов при сериализации
    private User user;

    @Transient // Не сохраняется в базе данных
    private String username;

    @Transient
    private Map<String, BigDecimal> incomeByCategory;

    @Transient
    private Map<String, BigDecimal> spendingByCategory;

    // Getters and Setters
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.username = user.getUsername();
        }
    }

    public String getUsername() {
        return username;
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

    @Override
    public String toString() {
        return "Report{" +
                "id=" + getId() +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", username='" + username + '\'' +
                ", incomeByCategory=" + incomeByCategory +
                ", spendingByCategory=" + spendingByCategory +
                '}';
    }
}

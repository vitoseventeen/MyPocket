package cz.cvut.fel.ear.stepavi2_havriboh.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "reports")
@NamedQueries({
        @NamedQuery(
                name = "Report.findByAccountId",
                query = "SELECT r FROM Report r WHERE r.account.id = :accountId"
        ),
        @NamedQuery(
                name = "Report.findByDateRange",
                query = "SELECT r FROM Report r WHERE r.fromDate >= :fromDate AND r.toDate <= :toDate"
        ),
        @NamedQuery(
                name = "Report.findByAccountAndDateRange",
                query = "SELECT r FROM Report r WHERE r.account.id = :accountId AND r.fromDate >= :fromDate AND r.toDate <= :toDate"
        )
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report extends AbstractEntity {

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = true)
    @OrderBy("id ASC")
    private Account account;

    @JsonProperty("account_id")
    public Integer getAccountId() {
        return account != null ? account.getId() : null;
    }

    @Override
    public String toString() {
        return "Report{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", account=" + account +
                '}';
    }

    @JsonProperty("expenses")
    public Map<String, String> getExpensesByCategories() {
        if (account == null) {
            return Map.of();
        }
        String currency = account.getBudget() != null ? String.valueOf(account.getBudget().getCurrency()) : "USD";
        return account.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE &&
                        !t.getDate().isBefore(fromDate) &&
                        !t.getDate().isAfter(toDate))
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory().getName() : "Uncategorized",
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() + " " + currency
                ));
    }

    @JsonProperty("incomes")
    public Map<String, String> getIncomesByCategories() {
        if (account == null) {
            return Map.of();
        }
        String currency = account.getBudget() != null ? String.valueOf(account.getBudget().getCurrency()) : "USD";
        return account.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME &&
                        !t.getDate().isBefore(fromDate) &&
                        !t.getDate().isAfter(toDate))
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory().getName() : "Uncategorized",
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() + " " + currency
                ));
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}

package cz.cvut.fel.ear.stepavi2_havriboh.main.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("USER")
@Table(name = "users")
@JsonPropertyOrder({"id", "username", "email", "password", "role", "subscriptionStartDate", "subscriptionEndDate"})
@NamedQueries({
        @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
        @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
})
public class User extends AbstractEntity {

    @Basic(optional = false)
    @Column(name = "username", nullable = false, unique = true)
    protected String username;

    @Basic(optional = false)
    @Column(name = "email", nullable = false, unique = true)
    protected String email;

    @Basic(optional = false)
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @Column(name = "subscription_start_date", nullable = true)
    private LocalDate subscriptionStartDate;

    @Column(name = "subscription_end_date", nullable = true)
    private LocalDate subscriptionEndDate;

    @ManyToMany(mappedBy = "users", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    @OrderBy("id ASC")
    private List<Account> accounts;

    public User() {
    }

    @JsonIgnore
    @Transient
    public boolean isSubscribed() {
        return subscriptionEndDate != null && subscriptionEndDate.isAfter(LocalDate.now());
    }


    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void erasePassword() {
        this.password = null;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", subscriptionStartDate=" + subscriptionStartDate +
                ", subscriptionEndDate=" + subscriptionEndDate +
                ", accounts=" + accounts +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @JsonIgnore
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public LocalDate getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public void setSubscriptionStartDate(LocalDate subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public LocalDate getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(LocalDate subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}

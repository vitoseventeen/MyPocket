package cz.cvut.sem.ear.stepavi2.havriboh.main.environment;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class Generator {

    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static int randomInt(int max) {
        return RAND.nextInt(max);
    }

    public static int randomInt(int min, int max) {
        assert min >= 0;
        assert min < max;

        int result;
        do {
            result = randomInt(max);
        } while (result < min);
        return result;
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static User generateUser() {
        final User user = new User();
        user.setEmail("email" + randomInt() + "@cvut.cz");
        user.setRole(Role.USER);
        user.setUsername("username" + randomInt());
        user.setPassword(Integer.toString(randomInt()));
        return user;
    }

    public static Budget generateBudget() {
        final Budget bud = new Budget();
        bud.setCurrentAmount(BigDecimal.valueOf(randomInt(1000)));
        bud.setCurrency("CZK");
        bud.setTargetAmount(BigDecimal.valueOf(randomInt(10000)));
        bud.setCategory(generateCategory());
        return bud;
    }

    public static Category generateCategory() {
        final Category cat = new Category();
        cat.setName("Category" + randomInt());
        cat.setDescription("Description" + randomInt());
        cat.setDefaultLimit(BigDecimal.valueOf(randomInt(1000)));
        cat.setBudget(generateBudget());
        cat.setTransactions(List.of(generateTransaction()));
        return cat;
    }

    public static Transaction generateTransaction() {
        final Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(randomInt(1000)));
        transaction.setCategory(generateCategory());
        transaction.setDate(LocalDate.now());
        transaction.setDescription("Description" + randomInt());
        transaction.setType(randomBoolean() ? TransactionType.INCOME : TransactionType.EXPENSE);
        transaction.setUser(generateUser());
        return transaction;
    }

    public static Account generateAccount() {
        final Account account = new Account();
        account.setBalance(BigDecimal.valueOf(randomInt(1000)));
        account.setCurrency("CZK");
        account.setUsers(List.of(generateUser()));
        account.setTransactions(List.of(generateTransaction()));
        return account;
    }

    public static Report generateReport() {
        final Report report = new Report();
        report.setUser(generateUser());
        report.setFromDate(LocalDate.of(2021, 1, 1));
        report.setToDate(LocalDate.of(2021, 12, 31));
        return report;
    }



}

package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Role;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;

@Component
public class SystemInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(SystemInitializer.class);

    /**
     * Default admin username
     */
    private static final String PREMIUM_USERNAME = "secret_premium_user";

    private final UserService userService;

    private final PlatformTransactionManager txManager;

    @Autowired
    public SystemInitializer(UserService userService,
                             PlatformTransactionManager txManager) {
        this.userService = userService;
        this.txManager = txManager;
    }

    @PostConstruct
    private void initSystem() {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.execute((status) -> {
            generatePremium();
            return null;
        });
    }

    private void generatePremium() {
        if (userService.exists(PREMIUM_USERNAME)) {
            return;
        }
        final User premium = new User();
        premium.setUsername(PREMIUM_USERNAME);
        premium.setEmail("premium@user.com");
        premium.setPassword("premium");
        premium.setRole(Role.PREMIUM);
        premium.setSubscribed(true);
        LOG.info("Generated premium user with credentials " + premium.getUsername() + "/" + premium.getPassword());
        userService.persist(premium);
    }
}

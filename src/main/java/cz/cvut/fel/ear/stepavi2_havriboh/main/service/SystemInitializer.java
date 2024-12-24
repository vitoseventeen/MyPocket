package cz.cvut.fel.ear.stepavi2_havriboh.main.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.config.AppConfig;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private static final String ADMIN_USERNAME = "admin";

    private final UserService userService;

    private final PlatformTransactionManager txManager;
    private final UserDao userDao;

    @Autowired
    public SystemInitializer(UserService userService,
                             PlatformTransactionManager txManager, UserDao userDao) {
        this.userService = userService;
        this.txManager = txManager;
        this.userDao = userDao;
    }

    @PostConstruct
    private void initSystem() {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.execute((status) -> {
            generateAdmin();
            return null;
        });
    }


    private final PasswordEncoder passwordEncoder = AppConfig.passwordEncoder();

    private void generateAdmin() {
        if (userService.exists(ADMIN_USERNAME)) {
            return;
        }
        final User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole(Role.ADMIN);
        admin.setSubscriptionStartDate(LocalDate.now());
        admin.setSubscriptionEndDate(LocalDate.now().plusYears(15));
        LOG.info("Generated admin user with credentials " + admin.getUsername() + "/" + admin.getPassword());
        userDao.persist(admin);
    }
}

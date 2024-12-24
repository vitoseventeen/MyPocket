package cz.cvut.fel.ear.stepavi2_havriboh.service;


import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.InvalidDateException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.NotPremiumUserException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.ReportNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Transaction;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.ReportService;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private TransactionDao transactionDao;

    @Test
    void getReportByIdWithValidData() {

    }

    @Test
    void getReportByIdThrowsExceptionWithInvalidData() {

    }

    @Test
    void deleteReportByIdRemovesReport() {

    }

    @Test
    void updateReportByIdUpdatesCorrectly() {

    }
}

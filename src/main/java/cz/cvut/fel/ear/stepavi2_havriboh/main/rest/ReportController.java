package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.AccountNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.InvalidDateException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.ReportNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/rest/reports")
public class ReportController {

    private final ReportService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }


    private boolean checkUserPerms(int userId) {
        User currentUser = Objects.requireNonNull(SecurityUtils.getCurrentUser(), "Current user cannot be null.");

        boolean isOwner = currentUser.getId().equals(userId);
        boolean isAdmin = currentUser.isAdmin();

        return !isOwner && !isAdmin;
    }

    // Only PREMIUM users can create reports
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PREMIUM')")
    @PostMapping
    public ResponseEntity<Object> createReport(
            @RequestParam int accountId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        try {
            if (checkUserPerms(accountId)) {
                throw new AccessDeniedException("Access denied");
            }
            reportService.createReport(accountId, fromDate, toDate);
            return ResponseEntity.status(201).body("Report created successfully.");
        } catch (AccountNotFoundException | InvalidDateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while creating the report.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getReportById(@PathVariable("id") int id) {
        try {
            Report report = reportService.getReportById(id);
            return ResponseEntity.ok(report);
        } catch (ReportNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving the report.");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        logger.info("Fetched {} reports", reports.size());
        return ResponseEntity.ok(reports);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PREMIUM')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteReportById(@PathVariable int id) {
        try {
            if (checkUserPerms(reportService.getReportById(id).getAccountId())) {
                throw new AccessDeniedException("Access denied");
            }
            reportService.deleteReportById(id);
            return ResponseEntity.ok("Report deleted successfully.");
        } catch (ReportNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while deleting the report.");
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_PREMIUM','ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateReportDateById(
            @PathVariable int id,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        try {
            if (checkUserPerms(reportService.getReportById(id).getAccountId())) {
                throw new AccessDeniedException("Access denied");
            }
            reportService.updateReportDateById(id, fromDate, toDate);
            return ResponseEntity.ok("Report dates updated successfully.");
        } catch (ReportNotFoundException | InvalidDateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while updating the report.");
        }
    }
}

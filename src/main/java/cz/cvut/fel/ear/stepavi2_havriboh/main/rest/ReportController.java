package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.ReportService;
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

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Only PREMIUM users can create reports
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PREMIUM')")
    @PostMapping
    public ResponseEntity<Object> createReport(
            @RequestParam int accountId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        try {
            reportService.createReport(accountId, fromDate, toDate);
            return ResponseEntity.status(201).body("Report created successfully.");
        } catch (AccountNotFoundException | NotPremiumUserException | InvalidDateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while creating the report.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getReportById(@PathVariable int id) {
        try {
            Report report = reportService.getReportById(id);
            checkReportPerms(id);
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
        try {
            List<Report> reports = reportService.getAllReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving all reports.");
        }
    }

    private void checkReportPerms(int id) {
        User currentUser = Objects.requireNonNull(SecurityUtils.getCurrentUser(), "Current user cannot be null.");

        boolean isOwnerOrAdmin = reportService.getReportById(id)
                .getAccount()
                .getUsers()
                .contains(currentUser) || currentUser.isAdmin();

        if (!isOwnerOrAdmin) {
            throw new AccessDeniedException("Forbidden");
        }
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PREMIUM')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteReportById(@PathVariable int id) {
        try {
            checkReportPerms(id);
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
            checkReportPerms(id);
            reportService.updateReportDateById(id, fromDate, toDate);
            return ResponseEntity.ok("Report dates updated successfully.");
        } catch (ReportNotFoundException | InvalidDateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while updating the report.");
        }
    }
}

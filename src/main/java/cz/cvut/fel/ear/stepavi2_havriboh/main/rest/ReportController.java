package cz.cvut.fel.ear.stepavi2_havriboh.main.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.model.UserDetails;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyRole('ADMIN','PREMIUM')")
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

    @PreAuthorize("hasAnyRole('ADMIN','PREMIUM')")
    @GetMapping("/{reportId}")
    public ResponseEntity<Object> getReportById(@PathVariable int reportId) {
        try {
            Report report = reportService.getReportById(reportId);
            checkReportPerms(reportId);
            return ResponseEntity.ok(report);
        } catch (ReportNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving the report.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getAllReports() {
        try {
            List<Report> reports = reportService.getAllReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving all reports.");
        }
    }

    private void checkReportPerms(int reportId) {
        User currentUser = Objects.requireNonNull(SecurityUtils.getCurrentUser(), "Current user cannot be null.");

        boolean isOwnerOrAdmin = reportService.getReportById(reportId)
                .getAccount()
                .getUsers()
                .contains(currentUser) || currentUser.isAdmin();

        if (!isOwnerOrAdmin) {
            throw new AccessDeniedException("Forbidden");
        }
    }


    @PreAuthorize("hasAnyRole('ADMIN','PREMIUM')")
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Object> deleteReportById(@PathVariable int reportId) {
        try {
            checkReportPerms(reportId);
            reportService.deleteReportById(reportId);
            return ResponseEntity.ok("Report deleted successfully.");
        } catch (ReportNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while deleting the report.");
        }
    }

    @PreAuthorize("hasAnyRole('PREMIUM','ADMIN')")
    @PutMapping("/{reportId}")
    public ResponseEntity<Object> updateReportDateById(
            @PathVariable int reportId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        try {
            checkReportPerms(reportId);
            reportService.updateReportDateById(reportId, fromDate, toDate);
            return ResponseEntity.ok("Report dates updated successfully.");
        } catch (ReportNotFoundException | InvalidDateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while updating the report.");
        }
    }
}

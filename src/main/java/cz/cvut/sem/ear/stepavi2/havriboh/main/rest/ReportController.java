package cz.cvut.sem.ear.stepavi2.havriboh.main.rest;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Report;
import cz.cvut.sem.ear.stepavi2.havriboh.main.service.ReportService;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.InvalidDateException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.ReportNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.UserNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.NotPremiumUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rest/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Object> createReport(
            @RequestParam int userId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        try {
            reportService.createReport(userId, fromDate, toDate);
            return ResponseEntity.status(201).body("Report created successfully.");
        } catch (UserNotFoundException | NotPremiumUserException | InvalidDateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while creating the report.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getReportsByUserId(@PathVariable int userId) {
        try {
            List<Report> reports = reportService.getReportsByUserId(userId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving reports.");
        }
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<Object> getReportsByUserIdAndDateRange(
            @PathVariable int userId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        try {
            List<Report> reports = reportService.getReportsByUserIdAndDateRange(userId, fromDate, toDate);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving reports.");
        }
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<Object> getReportById(@PathVariable int reportId) {
        try {
            Report report = reportService.getReportById(reportId);
            return ResponseEntity.ok(report);
        } catch (ReportNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving the report.");
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllReports() {
        try {
            List<Report> reports = reportService.getAllReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving all reports.");
        }
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Object> deleteReportById(@PathVariable int reportId) {
        try {
            reportService.deleteReportById(reportId);
            return ResponseEntity.ok("Report deleted successfully.");
        } catch (ReportNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while deleting the report.");
        }
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<Object> updateReportDateById(
            @PathVariable int reportId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        try {
            reportService.updateReportDateById(reportId, fromDate, toDate);
            return ResponseEntity.ok("Report dates updated successfully.");
        } catch (ReportNotFoundException | InvalidDateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while updating the report.");
        }
    }
}

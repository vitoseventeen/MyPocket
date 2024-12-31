package cz.cvut.fel.ear.stepavi2_havriboh.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.InvalidDateException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.ReportNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Account;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Role;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.User;
import cz.cvut.fel.ear.stepavi2_havriboh.main.rest.ReportController;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.ReportService;
import cz.cvut.fel.ear.stepavi2_havriboh.main.security.SecurityUtils;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReportControllerTest extends BaseControllerTestRunner {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getReportById_Success() {
        int reportId = 1;
        Report mockReport = new Report();

        when(reportService.getReportById(reportId)).thenReturn(mockReport);

        ResponseEntity<Object> response = reportController.getReportById(reportId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockReport, response.getBody());
    }

    @Test
    void getReportById_NotFound() {
        int reportId = 1;

        when(reportService.getReportById(reportId)).thenThrow(new ReportNotFoundException("Report not found."));

        ResponseEntity<Object> response = reportController.getReportById(reportId);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Report not found.", response.getBody());
    }

    @Test
    void getAllReports_Success() {
        List<Report> mockReports = Arrays.asList(new Report(), new Report());

        when(reportService.getAllReports()).thenReturn(mockReports);

        ResponseEntity<Object> response = reportController.getAllReports();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockReports, response.getBody());
    }

    @Test
    void deleteReportById_Success() {
        int reportId = 1;
        Report mockReport = new Report();


        when(reportService.getReportById(reportId)).thenReturn(mockReport);
        doNothing().when(reportService).deleteReportById(reportId);

        ResponseEntity<Object> response = reportController.deleteReportById(reportId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Report deleted successfully.", response.getBody());
    }

    @Test
    void deleteReportById_NotFound() {
        int reportId = 1;

        when(reportService.getReportById(reportId)).thenThrow(new ReportNotFoundException("Report not found."));

        ResponseEntity<Object> response = reportController.deleteReportById(reportId);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Report not found.", response.getBody());
    }

    @Test
    void updateReportDateById_Success() {
        int reportId = 1;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);
        Report mockReport = new Report();

        when(reportService.getReportById(reportId)).thenReturn(mockReport);
        doNothing().when(reportService).updateReportDateById(reportId, fromDate, toDate);

        ResponseEntity<Object> response = reportController.updateReportDateById(reportId, fromDate, toDate);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Report dates updated successfully.", response.getBody());
        verify(reportService).updateReportDateById(reportId, fromDate, toDate);
    }


    @Test
    void updateReportDateById_InvalidDate() {
        int reportId = 1;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2022, 12, 31);
        Report mockReport = new Report();


        when(reportService.getReportById(reportId)).thenReturn(mockReport);
        doThrow(new InvalidDateException("Invalid date range.")).when(reportService).updateReportDateById(reportId, fromDate, toDate);

        ResponseEntity<Object> response = reportController.updateReportDateById(reportId, fromDate, toDate);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid date range.", response.getBody());
    }

}

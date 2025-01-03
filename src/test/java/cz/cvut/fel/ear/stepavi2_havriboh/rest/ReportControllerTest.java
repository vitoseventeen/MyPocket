package cz.cvut.fel.ear.stepavi2_havriboh.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.ReportNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import cz.cvut.fel.ear.stepavi2_havriboh.main.rest.ReportController;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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


}

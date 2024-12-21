package cvut.ear.stepavi2_havriboh.rest;

import cvut.ear.stepavi2_havriboh.main.exception.InvalidDateException;
import cvut.ear.stepavi2_havriboh.main.exception.ReportNotFoundException;
import cvut.ear.stepavi2_havriboh.main.model.Report;
import cvut.ear.stepavi2_havriboh.main.rest.ReportController;
import cvut.ear.stepavi2_havriboh.main.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReportControllerTest extends BaseControllerTestRunner {

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = Mockito.mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);
        super.setUp(reportController);
    }

    @Test
    void createReport_shouldReturn201OnSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/rest/reports")
                        .param("userId", "1")
                        .param("fromDate", "2023-01-01")
                        .param("toDate", "2023-12-31"))
                .andExpect(status().isCreated())
                .andExpect(content().string("\"Report created successfully.\""));

        verify(reportService, times(1)).createReport(1, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
    }

    @Test
    void createReport_shouldReturnBadRequestInvalidDate() throws Exception {
        doThrow(new InvalidDateException("From date must be before to date"))
                .when(reportService).createReport(anyInt(), any(LocalDate.class), any(LocalDate.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/reports")
                        .param("userId", "1")
                        .param("fromDate", "2023-12-31")
                        .param("toDate", "2023-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("\"From date must be before to date\""));
    }

    @Test
    void getReportById_shouldReturnReportWhenFound() throws Exception {
        Report report = new Report();
        report.setFromDate(LocalDate.of(2023, 1, 1));
        report.setToDate(LocalDate.of(2023, 12, 31));

        when(reportService.getReportById(1)).thenReturn(report);

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromDate").value("2023-01-01"))
                .andExpect(jsonPath("$.toDate").value("2023-12-31"));
    }

    @Test
    void getReportById_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new ReportNotFoundException("Report not found"))
                .when(reportService).getReportById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/reports/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Report not found\""));
    }

    @Test
    void updateReportDateById_shouldReturn200OnSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/rest/reports/1")
                        .param("fromDate", "2023-01-01")
                        .param("toDate", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Report dates updated successfully.\""));

        verify(reportService, times(1)).updateReportDateById(1, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
    }

    @Test
    void updateReportDateById_shouldReturnBadRequestInvalidDate() throws Exception {
        doThrow(new InvalidDateException("From date must be before to date"))
                .when(reportService).updateReportDateById(anyInt(), any(LocalDate.class), any(LocalDate.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/reports/1")
                        .param("fromDate", "2023-12-31")
                        .param("toDate", "2023-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("\"From date must be before to date\""));
    }

    @Test
    void deleteReport_shouldReturn200OnSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/reports/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Report deleted successfully.\""));

        verify(reportService, times(1)).deleteReportById(1);
    }

    @Test
    void deleteReport_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new ReportNotFoundException("Report not found"))
                .when(reportService).deleteReportById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/reports/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Report not found\""));
    }
}

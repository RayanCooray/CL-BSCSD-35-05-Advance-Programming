package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.ReportDAO;
import lk.sunrise.dentalclinic.dto.ReportRequestDTO;
import lk.sunrise.dentalclinic.dto.RevenueReportDTO;
import lk.sunrise.dentalclinic.entity.Appointment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReportModelTest {

    @Test
    void dailyDelegatesRequestToReportDao() throws Exception {
        ReportDAO reportDAO = mock(ReportDAO.class);
        ReportModel model = new ReportModel(reportDAO);
        ReportRequestDTO request = new ReportRequestDTO(LocalDate.of(2026, 8, 27), LocalDate.of(2026, 8, 27), null, "PDF");
        List<Appointment> appointments = List.of(new Appointment());
        when(reportDAO.dailyAppointments(request)).thenReturn(appointments);

        assertEquals(appointments, model.daily(request));
        verify(reportDAO).dailyAppointments(request);
    }

    @Test
    void monthlyDelegatesYearAndMonthToReportDao() throws Exception {
        ReportDAO reportDAO = mock(ReportDAO.class);
        ReportModel model = new ReportModel(reportDAO);
        RevenueReportDTO revenue = new RevenueReportDTO(2, new BigDecimal("10000.00"), new BigDecimal("500.00"), new BigDecimal("7000.00"), new BigDecimal("3000.00"));
        when(reportDAO.monthlyRevenue(2026, 8)).thenReturn(revenue);

        assertEquals(revenue, model.monthly(2026, 8));
        verify(reportDAO).monthlyRevenue(2026, 8);
    }
}

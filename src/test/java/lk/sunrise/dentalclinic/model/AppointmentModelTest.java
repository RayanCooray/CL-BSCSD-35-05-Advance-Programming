package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.AppointmentDAO;
import lk.sunrise.dentalclinic.dao.DentistDAO;
import lk.sunrise.dentalclinic.dao.PatientDAO;
import lk.sunrise.dentalclinic.dao.TreatmentDAO;
import lk.sunrise.dentalclinic.dto.AppointmentDTO;
import lk.sunrise.dentalclinic.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppointmentModelTest {

    @Test
    void createWithValidAppointmentSavesScheduledAppointment() throws Exception {
        AppointmentDAO appointmentDAO = mock(AppointmentDAO.class);
        PatientDAO patientDAO = mock(PatientDAO.class);
        DentistDAO dentistDAO = mock(DentistDAO.class);
        TreatmentDAO treatmentDAO = mock(TreatmentDAO.class);
        AppointmentModel model = new AppointmentModel(appointmentDAO, patientDAO, dentistDAO, treatmentDAO);
        AppointmentDTO dto = appointmentDto();
        when(patientDAO.findById(1)).thenReturn(Optional.of(patient()));
        when(dentistDAO.findById(2)).thenReturn(Optional.of(dentist()));
        when(treatmentDAO.findById(3)).thenReturn(Optional.of(treatment()));
        when(appointmentDAO.existsConflict(2, dto.getDate(), dto.getStart(), dto.getEnd())).thenReturn(false);
        when(appointmentDAO.generateNextCode()).thenReturn("APP-000001");
        when(appointmentDAO.save(any(Appointment.class))).thenReturn(true);

        AppointmentDTO result = model.create(dto);

        assertEquals("APP-000001", result.getAppointmentNo());
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
        verify(appointmentDAO).save(any(Appointment.class));
    }

    @Test
    void createOutsideDentistWorkingHoursIsRejected() throws Exception {
        AppointmentDAO appointmentDAO = mock(AppointmentDAO.class);
        PatientDAO patientDAO = mock(PatientDAO.class);
        DentistDAO dentistDAO = mock(DentistDAO.class);
        TreatmentDAO treatmentDAO = mock(TreatmentDAO.class);
        AppointmentModel model = new AppointmentModel(appointmentDAO, patientDAO, dentistDAO, treatmentDAO);
        AppointmentDTO dto = appointmentDto();
        dto.setStart(LocalTime.of(18, 0));
        dto.setEnd(LocalTime.of(18, 30));
        when(patientDAO.findById(1)).thenReturn(Optional.of(patient()));
        when(dentistDAO.findById(2)).thenReturn(Optional.of(dentist()));
        when(treatmentDAO.findById(3)).thenReturn(Optional.of(treatment()));

        assertThrows(IllegalArgumentException.class, () -> model.create(dto));
        verify(appointmentDAO, never()).save(any(Appointment.class));
    }

    @Test
    void createWithDentistConflictIsRejected() throws Exception {
        AppointmentDAO appointmentDAO = mock(AppointmentDAO.class);
        PatientDAO patientDAO = mock(PatientDAO.class);
        DentistDAO dentistDAO = mock(DentistDAO.class);
        TreatmentDAO treatmentDAO = mock(TreatmentDAO.class);
        AppointmentModel model = new AppointmentModel(appointmentDAO, patientDAO, dentistDAO, treatmentDAO);
        AppointmentDTO dto = appointmentDto();
        when(patientDAO.findById(1)).thenReturn(Optional.of(patient()));
        when(dentistDAO.findById(2)).thenReturn(Optional.of(dentist()));
        when(treatmentDAO.findById(3)).thenReturn(Optional.of(treatment()));
        when(appointmentDAO.existsConflict(2, dto.getDate(), dto.getStart(), dto.getEnd())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> model.create(dto));
        verify(appointmentDAO, never()).save(any(Appointment.class));
    }

    @Test
    void updateWithEndBeforeStartIsRejected() {
        AppointmentModel model = new AppointmentModel(mock(AppointmentDAO.class), mock(PatientDAO.class), mock(DentistDAO.class), mock(TreatmentDAO.class));
        AppointmentDTO dto = appointmentDto();
        dto.setAppointmentId(4);
        dto.setEnd(LocalTime.of(8, 30));

        assertThrows(IllegalArgumentException.class, () -> model.update(dto));
    }

    private AppointmentDTO appointmentDto() {
        return new AppointmentDTO(0, null, 1, 2, 3, LocalDate.of(2026, 8, 27), LocalTime.of(10, 0), LocalTime.of(10, 30), null, "Routine");
    }

    private Patient patient() {
        return new Patient(1, "PAT-000001", "Nimal Perera", null, Gender.MALE, "0712345678", null, null, null, null);
    }

    private Dentist dentist() {
        return new Dentist(2, "DEN-000001", "Dr Silva", "SLMC-1", "Orthodontics", "0711111111", "dentist@test.lk", new BigDecimal("2500.00"), LocalTime.of(9, 0), LocalTime.of(17, 0), true);
    }

    private Treatment treatment() {
        return new Treatment(3, "TRT-000001", "Cleaning", "Routine cleaning", "Preventive", new BigDecimal("5000.00"), 30, true);
    }
}

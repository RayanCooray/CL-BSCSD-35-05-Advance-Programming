package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.PatientDAO;
import lk.sunrise.dentalclinic.dao.TreatmentDAO;
import lk.sunrise.dentalclinic.dao.TreatmentRecordDAO;
import lk.sunrise.dentalclinic.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TreatmentModelTest {

    @Test
    void createWithValidTreatmentGeneratesCodeAndSaves() throws Exception {
        TreatmentDAO treatmentDAO = mock(TreatmentDAO.class);
        TreatmentModel model = new TreatmentModel(treatmentDAO, mock(PatientDAO.class), mock(TreatmentRecordDAO.class));
        when(treatmentDAO.existsByName("Cleaning", 0)).thenReturn(false);
        when(treatmentDAO.generateNextCode()).thenReturn("TRT-000001");
        when(treatmentDAO.save(any(Treatment.class))).thenAnswer(invocation -> {
            Treatment treatment = invocation.getArgument(0);
            treatment.setTreatmentId(3);
            return true;
        });

        Treatment result = model.create("Cleaning", "Routine cleaning", "Preventive", new BigDecimal("5000.00"), 30, true);

        assertEquals(3, result.getTreatmentId());
        assertEquals("TRT-000001", result.getTreatmentCode());
        assertEquals("Cleaning", result.getName());
    }

    @Test
    void createWithDuplicateTreatmentNameIsRejected() throws Exception {
        TreatmentDAO treatmentDAO = mock(TreatmentDAO.class);
        TreatmentModel model = new TreatmentModel(treatmentDAO, mock(PatientDAO.class), mock(TreatmentRecordDAO.class));
        when(treatmentDAO.existsByName("Cleaning", 0)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> model.create("Cleaning", null, "Preventive", BigDecimal.TEN, 30, true));
        verify(treatmentDAO, never()).save(any(Treatment.class));
    }

    @Test
    void createWithInvalidDurationIsRejected() {
        TreatmentModel model = new TreatmentModel(mock(TreatmentDAO.class), mock(PatientDAO.class), mock(TreatmentRecordDAO.class));

        assertThrows(IllegalArgumentException.class, () -> model.create("Cleaning", null, "Preventive", BigDecimal.TEN, 0, true));
    }

    @Test
    void saveTreatmentRecordForIncompleteAppointmentIsRejected() {
        TreatmentModel model = new TreatmentModel(mock(TreatmentDAO.class), mock(PatientDAO.class), mock(TreatmentRecordDAO.class));
        TreatmentRecord record = treatmentRecord(AppointmentStatus.SCHEDULED);

        assertThrows(IllegalArgumentException.class, () -> model.save(record));
    }

    @Test
    void saveTreatmentRecordForCompletedAppointmentPersistsOnce() throws Exception {
        TreatmentRecordDAO recordDAO = mock(TreatmentRecordDAO.class);
        TreatmentModel model = new TreatmentModel(mock(TreatmentDAO.class), mock(PatientDAO.class), recordDAO);
        TreatmentRecord record = treatmentRecord(AppointmentStatus.COMPLETED);
        when(recordDAO.findByAppointment(20)).thenReturn(List.of());
        when(recordDAO.save(record)).thenReturn(true);

        assertTrue(model.save(record));
        verify(recordDAO).save(record);
    }

    @Test
    void deleteUsedTreatmentReportsDeactivateMessage() throws Exception {
        TreatmentDAO treatmentDAO = mock(TreatmentDAO.class);
        TreatmentModel model = new TreatmentModel(treatmentDAO, mock(PatientDAO.class), mock(TreatmentRecordDAO.class));
        when(treatmentDAO.findById(3)).thenReturn(Optional.of(new Treatment()));
        when(treatmentDAO.delete(3)).thenThrow(new RuntimeException("foreign key constraint fails"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> model.delete(3));

        assertTrue(exception.getMessage().contains("Deactivate it instead of deleting it"));
    }

    private TreatmentRecord treatmentRecord(AppointmentStatus status) {
        Patient patient = new Patient(1, "PAT-000001", "Nimal Perera", null, Gender.MALE, "0712345678", null, null, null, null);
        Dentist dentist = new Dentist(2, "DEN-000001", "Dr Silva", "SLMC-1", null, null, null, new BigDecimal("2500.00"), LocalTime.of(9, 0), LocalTime.of(17, 0), true);
        Treatment treatment = new Treatment(3, "TRT-000001", "Cleaning", null, "Preventive", new BigDecimal("5000.00"), 30, true);
        Appointment appointment = new Appointment(20, "APP-000020", patient, dentist, treatment, LocalDate.of(2026, 8, 27), LocalTime.of(10, 0), LocalTime.of(10, 30), status, null, null);
        return new TreatmentRecord(0, patient, dentist, treatment, appointment, LocalDate.of(2026, 8, 27), "Done", new BigDecimal("5000.00"));
    }
}

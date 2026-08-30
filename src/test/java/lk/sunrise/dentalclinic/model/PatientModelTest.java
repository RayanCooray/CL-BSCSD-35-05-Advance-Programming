package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.PatientDAO;
import lk.sunrise.dentalclinic.dto.PatientDTO;
import lk.sunrise.dentalclinic.entity.Gender;
import lk.sunrise.dentalclinic.entity.Patient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientModelTest {

    @Test
    void registerWithValidPatientGeneratesCodeAndSaves() throws Exception {
        PatientDAO patientDAO = mock(PatientDAO.class);
        PatientModel model = new PatientModel(patientDAO);
        PatientDTO dto = new PatientDTO(0, null, "Nimal Perera", LocalDate.of(1990, 1, 10), Gender.MALE, "0712345678", "nimal@test.lk", "Colombo", "None");
        when(patientDAO.existsByContact("0712345678")).thenReturn(false);
        when(patientDAO.generateNextCode()).thenReturn("PAT-000001");
        when(patientDAO.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setPatientId(10);
            return true;
        });

        PatientDTO result = model.register(dto);

        assertEquals(10, result.getPatientId());
        assertEquals("PAT-000001", result.getPatientCode());
        assertEquals("Nimal Perera", result.getFullName());
        verify(patientDAO).save(any(Patient.class));
    }

    @Test
    void registerWithBlankPatientNameThrowsValidationError() {
        PatientModel model = new PatientModel(mock(PatientDAO.class));
        PatientDTO dto = new PatientDTO(0, null, " ", null, Gender.FEMALE, "0712345678", null, null, null);

        assertThrows(IllegalArgumentException.class, () -> model.register(dto));
    }

    @Test
    void registerWithDuplicateContactIsRejected() throws Exception {
        PatientDAO patientDAO = mock(PatientDAO.class);
        PatientModel model = new PatientModel(patientDAO);
        PatientDTO dto = new PatientDTO(0, null, "Nimal Perera", null, Gender.MALE, "0712345678", null, null, null);
        when(patientDAO.existsByContact("0712345678")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> model.register(dto));
        verify(patientDAO, never()).save(any(Patient.class));
    }

    @Test
    void registerWithDuplicateEmailIsRejected() throws Exception {
        PatientDAO patientDAO = mock(PatientDAO.class);
        PatientModel model = new PatientModel(patientDAO);
        PatientDTO dto = new PatientDTO(0, null, "Nimal Perera", null, Gender.MALE, "0712345678", "nimal@test.lk", null, null);
        when(patientDAO.existsByContact("0712345678")).thenReturn(false);
        when(patientDAO.existsByEmail("nimal@test.lk")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> model.register(dto));
        verify(patientDAO, never()).save(any(Patient.class));
    }

    @Test
    void updateWithMissingPatientThrowsNotFoundError() throws Exception {
        PatientDAO patientDAO = mock(PatientDAO.class);
        PatientModel model = new PatientModel(patientDAO);
        PatientDTO dto = new PatientDTO(99, "PAT-000099", "Nimal Perera", null, Gender.MALE, "0712345678", null, null, null);
        when(patientDAO.existsByContactExcept("0712345678", 99)).thenReturn(false);
        when(patientDAO.findById(99)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> model.update(dto));
    }
}

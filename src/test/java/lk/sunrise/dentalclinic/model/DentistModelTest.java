package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.DentistDAO;
import lk.sunrise.dentalclinic.dto.DentistDTO;
import lk.sunrise.dentalclinic.entity.Dentist;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DentistModelTest {

    @Test
    void registerWithValidDentistGeneratesCodeAndSaves() throws Exception {
        DentistDAO dentistDAO = mock(DentistDAO.class);
        DentistModel model = new DentistModel(dentistDAO);
        DentistDTO dto = validDentist(0, "Dr Silva", "SLMC-1");
        when(dentistDAO.findBySlmcNumber("SLMC-1")).thenReturn(Optional.empty());
        when(dentistDAO.generateNextCode()).thenReturn("DEN-000001");
        when(dentistDAO.save(any(Dentist.class))).thenAnswer(invocation -> {
            Dentist dentist = invocation.getArgument(0);
            dentist.setDentistId(5);
            return true;
        });

        DentistDTO result = model.register(dto);

        assertEquals(5, result.getDentistId());
        assertEquals("DEN-000001", result.getDentistCode());
        assertEquals("Dr Silva", result.getFullName());
    }

    @Test
    void registerWithDuplicateSlmcIsRejected() throws Exception {
        DentistDAO dentistDAO = mock(DentistDAO.class);
        DentistModel model = new DentistModel(dentistDAO);
        when(dentistDAO.findBySlmcNumber("SLMC-1")).thenReturn(Optional.of(new Dentist()));

        assertThrows(IllegalArgumentException.class, () -> model.register(validDentist(0, "Dr Silva", "SLMC-1")));
        verify(dentistDAO, never()).save(any(Dentist.class));
    }

    @Test
    void registerWithDuplicateEmailIsRejected() throws Exception {
        DentistDAO dentistDAO = mock(DentistDAO.class);
        DentistModel model = new DentistModel(dentistDAO);
        when(dentistDAO.findBySlmcNumber("SLMC-1")).thenReturn(Optional.empty());
        when(dentistDAO.existsByEmail("dentist@test.lk")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> model.register(validDentist(0, "Dr Silva", "SLMC-1")));
        verify(dentistDAO, never()).save(any(Dentist.class));
    }

    @Test
    void registerWithInvalidWorkingHoursThrowsValidationError() {
        DentistModel model = new DentistModel(mock(DentistDAO.class));
        DentistDTO dto = validDentist(0, "Dr Silva", "SLMC-1");
        dto.setWorkingHoursStart(LocalTime.of(17, 0));
        dto.setWorkingHoursEnd(LocalTime.of(9, 0));

        assertThrows(IllegalArgumentException.class, () -> model.register(dto));
    }

    @Test
    void registerWithNegativeFeeThrowsValidationError() {
        DentistModel model = new DentistModel(mock(DentistDAO.class));
        DentistDTO dto = validDentist(0, "Dr Silva", "SLMC-1");
        dto.setConsultationFee(new BigDecimal("-1.00"));

        assertThrows(IllegalArgumentException.class, () -> model.register(dto));
    }

    private DentistDTO validDentist(int id, String name, String slmc) {
        return new DentistDTO(id, null, name, slmc, "Orthodontics", "0712345678", "dentist@test.lk", new BigDecimal("2500.00"), LocalTime.of(9, 0), LocalTime.of(17, 0), true);
    }
}

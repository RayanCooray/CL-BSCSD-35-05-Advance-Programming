package lk.sunrise.dentalclinic.ui.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    @Test
    void validSriLankanMobileNumberIsAccepted() {
        assertDoesNotThrow(() -> Validation.phone("0712345678"));
        assertDoesNotThrow(() -> Validation.phone("+94712345678"));
    }

    @Test
    void invalidSriLankanMobileNumberIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Validation.phone("12345"));
    }

    @Test
    void validEmailIsAcceptedAndInvalidEmailIsRejected() {
        assertDoesNotThrow(() -> Validation.email("patient@test.lk"));
        assertThrows(IllegalArgumentException.class, () -> Validation.email("patient@test"));
    }

    @Test
    void decimalParsesMoneyValueWithTwoDecimalPlaces() {
        assertEquals(new BigDecimal("1250.50"), Validation.decimal("1250.50", "Amount"));
    }

    @Test
    void invalidPatientCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Validation.patientCode("PAT-1"));
    }

    @Test
    void dateOfBirthMustBeBeforeToday() {
        assertDoesNotThrow(() -> Validation.dateOfBirth(LocalDate.now().minusDays(1)));
        assertThrows(IllegalArgumentException.class, () -> Validation.dateOfBirth(LocalDate.now()));
        assertThrows(IllegalArgumentException.class, () -> Validation.dateOfBirth(LocalDate.now().plusDays(1)));
    }
}

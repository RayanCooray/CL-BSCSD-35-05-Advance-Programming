package lk.sunrise.dentalclinic.ui.util;

import javafx.scene.control.Control;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

public final class Validation {
    private Validation() {
    }

    private static final Pattern NAME = Pattern.compile("^[A-Za-z][A-Za-z .'-]{2,149}$");
    private static final Pattern PHONE = Pattern.compile("^(?:\\+94|0)(?:7\\d{8}|\\d{9})$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern CODE = Pattern.compile("^PAT-\\d{6}$");
    private static final Pattern TIME = Pattern.compile("^(?:[01]\\d|2[0-3]):[0-5]\\d$");
    private static final Pattern DECIMAL = Pattern.compile("^\\d+(?:\\.\\d{1,2})?$");

    public static void required(Control control, String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
    }

    public static void name(String value) {
        if (value == null || !NAME.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("Enter a valid full name.");
        }
    }

    public static void phone(String value) {
        if (value == null || !PHONE.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("Enter a valid Sri Lankan mobile number (07XXXXXXXX or +947XXXXXXXX).");
        }
    }

    public static void email(String value) {
        if (value != null && !value.isBlank() && !EMAIL.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
    }

    public static void dateOfBirth(LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("Date of birth is required.");
        }
        if (!value.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth must be before today.");
        }
    }

    public static void markInvalid(Control control, boolean invalid) {
        if (invalid) {
            if (!control.getStyleClass().contains("input-error")) {
                control.getStyleClass().add("input-error");
            }
        } else {
            control.getStyleClass().remove("input-error");
        }
    }

    public static void patientCode(String value) {
        if (value == null || !CODE.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("Patient code must look like PAT-000001.");
        }
    }

    public static void time(String value, String field) {
        if (value == null || !TIME.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException(field + " must use HH:mm format.");
        }
    }

    public static BigDecimal decimal(String value, String field) {
        String v = value == null || value.isBlank() ? "0" : value.trim();
        if (!DECIMAL.matcher(v).matches()) {
            throw new IllegalArgumentException(field + " must be a valid positive number.");
        }
        return new BigDecimal(v);
    }
}

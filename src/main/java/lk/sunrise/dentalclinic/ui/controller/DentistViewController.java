package lk.sunrise.dentalclinic.ui.controller;

import lk.sunrise.dentalclinic.controller.DentistController;
import lk.sunrise.dentalclinic.dto.DentistDTO;
import lk.sunrise.dentalclinic.entity.Dentist;
import lk.sunrise.dentalclinic.ui.util.Ui;
import lk.sunrise.dentalclinic.ui.util.Validation;
import lk.sunrise.dentalclinic.ui.view.DentistView;

import java.math.BigDecimal;
import java.time.LocalTime;

public class DentistViewController {
    private final DentistView view;
    private final DentistController controller = new DentistController();

    public DentistViewController(DentistView v) {
        view = v;
    }

    public void initialize() {
        view.save().setOnAction(e -> save());
        view.update().setOnAction(e -> update());
        bindRealtimeValidation();
        search();
    }

    public void search() {
        try {
            view.table().getItems().setAll(controller.search(view.search().getText()));
        } catch (Exception e) {
            applySubmitError(e);
            Ui.error(view.root(), e);
        }
    }

    public void loadSelected() {
        Dentist d = view.table().getSelectionModel().getSelectedItem();
        if (d == null) return;
        view.name().setText(d.getFullName());
        view.slmc().setText(d.getSlmcNumber());
        view.special().setText(d.getSpecialization());
        view.contact().setText(d.getContactNumber());
        view.email().setText(d.getEmail());
        view.fee().setText(d.getConsultationFee().toPlainString());
        view.start().setText(d.getWorkingHoursStart().toString());
        view.end().setText(d.getWorkingHoursEnd().toString());
        view.available().setSelected(d.isAvailable());
        refreshValidationMarkers();
    }

    private DentistDTO dto(int id) {
        validate();
        return new DentistDTO(id, null, view.name().getText().trim(), view.slmc().getText().trim(), view.special().getText().trim(), view.contact().getText().trim(), view.email().getText().trim(), new BigDecimal(view.fee().getText().trim()), LocalTime.parse(view.start().getText().trim()), LocalTime.parse(view.end().getText().trim()), view.available().isSelected());
    }

    public void save() {
        try {
            DentistDTO d = controller.register(dto(0));
            search();
            Ui.notify(view.root(), "Dentist added", d.getDentistCode() + " was saved.", false);
        } catch (Exception e) {
            applySubmitError(e);
            Ui.error(view.root(), e);
        }
    }

    public void update() {
        try {
            Dentist selected = view.table().getSelectionModel().getSelectedItem();
            if (selected == null) throw new IllegalArgumentException("Select a dentist first.");
            controller.update(dto(selected.getDentistId()));
            search();
            Ui.notify(view.root(), "Dentist updated", "Changes saved.", false);
        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    private void validate() {
        refreshValidationMarkers();
        Validation.name(view.name().getText());
        Validation.required(view.slmc(), view.slmc().getText(), "SLMC number");
        Validation.required(view.special(), view.special().getText(), "Specialization");
        Validation.phone(view.contact().getText());
        Validation.email(view.email().getText());
        Validation.decimal(view.fee().getText(), "Consultation fee");
        Validation.time(view.start().getText(), "Start time");
        Validation.time(view.end().getText(), "End time");
        if (!LocalTime.parse(view.end().getText().trim()).isAfter(LocalTime.parse(view.start().getText().trim()))) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
    }

    private void bindRealtimeValidation() {
        view.name().textProperty().addListener((obs, old, value) -> markText(() -> Validation.name(value), view.name(), view.nameError()));
        view.slmc().textProperty().addListener((obs, old, value) -> markText(() -> Validation.required(view.slmc(), value, "SLMC number"), view.slmc(), view.slmcError()));
        view.special().textProperty().addListener((obs, old, value) -> markText(() -> Validation.required(view.special(), value, "Specialization"), view.special(), view.specialError()));
        view.contact().textProperty().addListener((obs, old, value) -> markText(() -> Validation.phone(value), view.contact(), view.contactError()));
        view.email().textProperty().addListener((obs, old, value) -> markText(() -> Validation.email(value), view.email(), view.emailError()));
        view.fee().textProperty().addListener((obs, old, value) -> markText(() -> Validation.decimal(value, "Consultation fee"), view.fee(), view.feeError()));
        view.start().textProperty().addListener((obs, old, value) -> refreshTimeMarkers());
        view.end().textProperty().addListener((obs, old, value) -> refreshTimeMarkers());
    }

    private void refreshValidationMarkers() {
        markText(() -> Validation.name(view.name().getText()), view.name(), view.nameError());
        markText(() -> Validation.required(view.slmc(), view.slmc().getText(), "SLMC number"), view.slmc(), view.slmcError());
        markText(() -> Validation.required(view.special(), view.special().getText(), "Specialization"), view.special(), view.specialError());
        markText(() -> Validation.phone(view.contact().getText()), view.contact(), view.contactError());
        markText(() -> Validation.email(view.email().getText()), view.email(), view.emailError());
        markText(() -> Validation.decimal(view.fee().getText(), "Consultation fee"), view.fee(), view.feeError());
        refreshTimeMarkers();
    }

    private void refreshTimeMarkers() {
        markText(() -> Validation.time(view.start().getText(), "Start time"), view.start(), view.startError());
        markText(() -> Validation.time(view.end().getText(), "End time"), view.end(), view.endError());
        try {
            LocalTime start = LocalTime.parse(view.start().getText().trim());
            LocalTime end = LocalTime.parse(view.end().getText().trim());
            boolean invalidRange = !end.isAfter(start);
            if (invalidRange) {
                setValidationMessage(view.start(), view.startError(), "Start time must be before end time.");
                setValidationMessage(view.end(), view.endError(), "End time must be after start time.");
            }
        } catch (Exception ignored) {
            // Individual time format validators already mark the broken field.
        }
    }

    private void markText(Runnable validator, javafx.scene.control.Control control, javafx.scene.control.Label error) {
        try {
            validator.run();
            setValidationMessage(control, error, null);
        } catch (IllegalArgumentException ex) {
            setValidationMessage(control, error, ex.getMessage());
        }
    }

    private void setValidationMessage(javafx.scene.control.Control control, javafx.scene.control.Label error, String message) {
        boolean invalid = message != null && !message.isBlank();
        Validation.markInvalid(control, invalid);
        error.setText(invalid ? message : "");
        error.setVisible(invalid);
        error.setManaged(invalid);
    }

    private void applySubmitError(Exception ex) {
        String message = ex.getMessage();
        if (message == null) return;
        String lower = message.toLowerCase();
        if (lower.contains("email")) {
            setValidationMessage(view.email(), view.emailError(), message);
        } else if (lower.contains("slmc")) {
            setValidationMessage(view.slmc(), view.slmcError(), message);
        }
    }
}

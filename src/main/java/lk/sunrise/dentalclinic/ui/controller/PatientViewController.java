package lk.sunrise.dentalclinic.ui.controller;

import lk.sunrise.dentalclinic.controller.PatientController;
import lk.sunrise.dentalclinic.dto.PatientDTO;
import lk.sunrise.dentalclinic.entity.Patient;
import lk.sunrise.dentalclinic.ui.util.Ui;
import lk.sunrise.dentalclinic.ui.util.Validation;
import lk.sunrise.dentalclinic.ui.view.PatientView;

public class PatientViewController {
    private final PatientView view;
    private final PatientController controller = new PatientController();
    private int selectedPatientId;

    public PatientViewController(PatientView view) {
        this.view = view;
    }

    public void initialize() {
        view.save().setOnAction(e -> register());
        view.update().setOnAction(e -> update());
        view.clear().setOnAction(e -> clear());
        bindRealtimeValidation();
        search();
    }

    public void register() {
        try {
            validate();
            PatientDTO dto = controller.register(dto(0));
            clear();
            search();
            Ui.notify(view.root(), "Patient registered", dto.getPatientCode() + " was saved.", false);
        } catch (Exception ex) {
            applySubmitError(ex);
            Ui.error(view.root(), ex);
        }
    }

    public void update() {
        try {
            if (selectedPatientId <= 0) throw new IllegalArgumentException("Select a patient record first.");
            validate();
            PatientDTO dto = controller.update(dto(selectedPatientId));
            search();
            view.mode().setText("Editing: " + dto.getPatientCode());
            Ui.notify(view.root(), "Patient updated", "Changes saved successfully.", false);
        } catch (Exception ex) {
            applySubmitError(ex);
            Ui.error(view.root(), ex);
        }
    }

    public void search() {
        try {
            view.table().getItems().setAll(controller.search(view.search().getText()));
        } catch (Exception ex) {
            Ui.error(view.root(), ex);
        }
    }

    public void loadSelected(Patient p) {
        if (p == null) return;
        selectedPatientId = p.getPatientId();
        view.name().setText(p.getFullName());
        view.contact().setText(p.getContactNumber());
        view.email().setText(p.getEmail());
        view.address().setText(p.getAddress());
        view.dob().setValue(p.getDateOfBirth());
        view.gender().setValue(p.getGender());
        view.history().setText(p.getMedicalHistory() == null ? "" : p.getMedicalHistory());
        view.mode().setText("Editing: " + p.getPatientCode());
        refreshValidationMarkers();
    }

    public void viewSelected() {
        Patient p = view.table().getSelectionModel().getSelectedItem();
        if (p == null) {
            Ui.error(view.root(), new IllegalArgumentException("Select a patient first."));
            return;
        }
        Ui.showPatientDetails(view.root(), p);
    }

    private void validate() {
        refreshValidationMarkers();
        Validation.name(view.name().getText());
        Validation.dateOfBirth(view.dob().getValue());
        if (view.gender().getValue() == null) throw new IllegalArgumentException("Gender is required.");
        Validation.phone(view.contact().getText());
        Validation.email(view.email().getText());
    }

    private PatientDTO dto(int id) {
        return new PatientDTO(id, null, view.name().getText().trim(), view.dob().getValue(), view.gender().getValue(),
                view.contact().getText().trim(), view.email().getText().trim(), view.address().getText().trim(), view.history().getText().trim());
    }

    private void clear() {
        selectedPatientId = 0;
        view.name().clear();
        view.contact().clear();
        view.email().clear();
        view.address().clear();
        view.history().clear();
        view.dob().setValue(null);
        view.gender().setValue(null);
        view.mode().setText("New patient");
        view.table().getSelectionModel().clearSelection();
        clearValidationMarkers();
    }

    private void bindRealtimeValidation() {
        view.name().textProperty().addListener((obs, old, value) -> markText(() -> Validation.name(value), view.name(), view.nameError()));
        view.contact().textProperty().addListener((obs, old, value) -> markText(() -> Validation.phone(value), view.contact(), view.contactError()));
        view.email().textProperty().addListener((obs, old, value) -> markText(() -> Validation.email(value), view.email(), view.emailError()));
        view.dob().valueProperty().addListener((obs, old, value) -> markText(() -> Validation.dateOfBirth(value), view.dob(), view.dobError()));
        view.gender().valueProperty().addListener((obs, old, value) -> setValidationMessage(view.gender(), view.genderError(), value == null ? "Gender is required." : null));
    }

    private void refreshValidationMarkers() {
        markText(() -> Validation.name(view.name().getText()), view.name(), view.nameError());
        markText(() -> Validation.phone(view.contact().getText()), view.contact(), view.contactError());
        markText(() -> Validation.email(view.email().getText()), view.email(), view.emailError());
        markText(() -> Validation.dateOfBirth(view.dob().getValue()), view.dob(), view.dobError());
        setValidationMessage(view.gender(), view.genderError(), view.gender().getValue() == null ? "Gender is required." : null);
    }

    private void markText(Runnable validator, javafx.scene.control.Control control, javafx.scene.control.Label error) {
        try {
            validator.run();
            setValidationMessage(control, error, null);
        } catch (IllegalArgumentException ex) {
            setValidationMessage(control, error, ex.getMessage());
        }
    }

    private void clearValidationMarkers() {
        setValidationMessage(view.name(), view.nameError(), null);
        setValidationMessage(view.contact(), view.contactError(), null);
        setValidationMessage(view.email(), view.emailError(), null);
        setValidationMessage(view.dob(), view.dobError(), null);
        setValidationMessage(view.gender(), view.genderError(), null);
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
        } else if (lower.contains("contact")) {
            setValidationMessage(view.contact(), view.contactError(), message);
        }
    }
}

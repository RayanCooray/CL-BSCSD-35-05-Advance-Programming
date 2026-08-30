package lk.sunrise.dentalclinic.ui.controller;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import lk.sunrise.dentalclinic.controller.AppointmentController;
import lk.sunrise.dentalclinic.controller.PatientController;
import lk.sunrise.dentalclinic.dto.AppointmentDTO;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.ui.util.Ui;
import lk.sunrise.dentalclinic.ui.util.Validation;
import lk.sunrise.dentalclinic.ui.view.AppointmentView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentViewController {

    private final AppointmentView view;

    private final AppointmentController controller =
            new AppointmentController();

    private final PatientController patients =
            new PatientController();

    private List<Patient> allPatients =
            new ArrayList<>();

    private int selectedId = 0;

    private boolean syncingPatients = false;


    public AppointmentViewController(AppointmentView view) {
        this.view = view;
    }


    public void initialize() {

        try {

            view.dentist().setItems(
                    FXCollections.observableArrayList(
                            controller.getAvailableDentists()
                    )
            );

            view.treatment().setItems(
                    FXCollections.observableArrayList(
                            controller.getActiveTreatments()
                    )
            );

            allPatients = patients.search("");

            configurePatientCombo(view.patient());
            configurePatientCombo(view.patientSearch());

            view.patient().setItems(
                    FXCollections.observableArrayList(allPatients)
            );

            view.patientSearch().setItems(
                    FXCollections.observableArrayList(allPatients)
            );


            view.patient()
                    .valueProperty()
                    .addListener((obs, oldValue, newValue) -> {

                        if (syncingPatients || newValue == null) {
                            return;
                        }

                        syncPatientSelection(
                                view.patientSearch(),
                                newValue
                        );
                        refreshValidationMarkers();
                    });


            view.patientSearch()
                    .valueProperty()
                    .addListener((obs, oldValue, newValue) -> {

                        if (syncingPatients || newValue == null) {
                            return;
                        }

                        syncPatientSelection(
                                view.patient(),
                                newValue
                        );
                        refreshValidationMarkers();
                    });


            installSearch(view.patient());
            installSearch(view.patientSearch());

            bindRealtimeValidation();


            view.create().setOnAction(e -> create());
            view.update().setOnAction(e -> update());
            view.clear().setOnAction(e -> clear());
            view.viewPatient().setOnAction(e -> viewPatient());


            load();

        } catch (Exception e) {

            Ui.error(
                    view.root(),
                    e
            );
        }
    }


    private void configurePatientCombo(
            ComboBox<Patient> combo) {

        combo.setEditable(true);

        combo.setPromptText(
                "Search patient code / name / contact..."
        );

        combo.setConverter(
                new StringConverter<>() {

                    @Override
                    public String toString(Patient patient) {

                        if (patient == null) {
                            return "";
                        }

                        return patient.getPatientCode()
                                + " — "
                                + patient.getFullName()
                                + " — "
                                + patient.getContactNumber();
                    }


                    @Override
                    public Patient fromString(String value) {

                        return findPatient(value);
                    }
                }
        );
    }


    private void installSearch(
            ComboBox<Patient> combo) {

        TextField editor = combo.getEditor();

        editor.textProperty().addListener(
                (obs, oldText, newText) -> {

                    if (syncingPatients) {
                        return;
                    }

                    String currentText =
                            newText == null
                                    ? ""
                                    : newText.trim();

                    Patient selected =
                            combo.getValue();

                    /*
                     * If JavaFX is simply displaying
                     * the currently selected patient,
                     * don't filter the list.
                     */
                    if (selected != null) {

                        String selectedText =
                                combo.getConverter()
                                        .toString(selected);

                        if (selectedText.equals(currentText)) {
                            return;
                        }
                    }

                    filterPatients(
                            combo,
                            currentText
                    );
                }
        );
    }


    private void filterPatients(
            ComboBox<Patient> combo,
            String text) {

        String query =
                text == null
                        ? ""
                        : text.trim().toLowerCase();


        List<Patient> filtered =
                allPatients.stream()
                        .filter(patient -> {

                            String value =
                                    (
                                            patient.getPatientCode()
                                                    + " "
                                                    + patient.getFullName()
                                                    + " "
                                                    + patient.getContactNumber()
                                    )
                                            .toLowerCase();

                            return value.contains(query);

                        })
                        .toList();


        Patient current =
                combo.getValue();


        combo.setItems(
                FXCollections.observableArrayList(
                        filtered
                )
        );


        /*
         * Keep the currently selected patient
         * if it is still inside the filtered list.
         */
        if (current != null
                && filtered.contains(current)) {

            combo.setValue(current);
        }


        if (!query.isBlank()) {
            combo.show();
        }
    }


    public void selectPatientFromMainField() {

        Patient patient =
                resolveEditorSelection(
                        view.patient()
                );

        if (patient != null) {

            setPatientOnBoth(patient);
        }
    }


    public void selectPatientFromSearchField() {

        Patient patient =
                resolveEditorSelection(
                        view.patientSearch()
                );

        if (patient != null) {

            setPatientOnBoth(patient);
        }
    }


    private Patient resolveEditorSelection(
            ComboBox<Patient> combo) {

        /*
         * First try the actual selected object.
         */
        Patient selected =
                combo.getValue();

        if (selected != null) {
            return selected;
        }


        /*
         * If the editable ComboBox only has text,
         * resolve that text against the database list.
         */
        String text =
                combo.getEditor()
                        .getText();


        Patient found =
                findPatient(text);


        if (found != null) {

            /*
             * IMPORTANT:
             * Put the actual Patient object back
             * into the ComboBox.
             */
            setComboValue(
                    combo,
                    found
            );

            return found;
        }


        return null;
    }


    private Patient findPatient(
            String text) {

        if (text == null
                || text.isBlank()) {

            return null;
        }


        String value =
                text.trim();


        String query =
                value.toLowerCase();


        return allPatients.stream()
                .filter(patient -> {

                    String display =
                            (
                                    patient.getPatientCode()
                                            + " — "
                                            + patient.getFullName()
                                            + " — "
                                            + patient.getContactNumber()
                            )
                                    .toLowerCase();


                    return patient.getPatientCode()
                            .equalsIgnoreCase(value)

                            || patient.getFullName()
                            .equalsIgnoreCase(value)

                            || patient.getContactNumber()
                            .equalsIgnoreCase(value)

                            || display.equals(query)

                            || patient.getPatientCode()
                            .toLowerCase()
                            .contains(query)

                            || patient.getFullName()
                            .toLowerCase()
                            .contains(query)

                            || patient.getContactNumber()
                            .toLowerCase()
                            .contains(query);

                })
                .findFirst()
                .orElse(null);
    }


    private void setPatientOnBoth(
            Patient patient) {

        if (patient == null) {
            return;
        }


        syncingPatients = true;

        try {

            setComboValue(
                    view.patient(),
                    patient
            );

            setComboValue(
                    view.patientSearch(),
                    patient
            );

        } finally {

            syncingPatients = false;
        }
    }


    private void syncPatientSelection(
            ComboBox<Patient> target,
            Patient patient) {

        if (patient == null) {
            return;
        }


        syncingPatients = true;

        try {

            setComboValue(
                    target,
                    patient
            );

        } finally {

            syncingPatients = false;
        }
    }


    private void setComboValue(
            ComboBox<Patient> combo,
            Patient patient) {

        if (patient == null) {
            return;
        }


        /*
         * Make sure the patient exists
         * in the ComboBox items.
         */
        if (!combo.getItems().contains(patient)) {

            combo.setItems(
                    FXCollections.observableArrayList(
                            allPatients
                    )
            );
        }


        /*
         * This is the important part.
         *
         * We set the actual Patient object,
         * not only the text.
         */
        combo.setValue(patient);


        /*
         * Also update the visible editor text.
         */
        if (combo.isEditable()) {

            combo.getEditor().setText(
                    combo.getConverter()
                            .toString(patient)
            );
        }


        combo.hide();
    }


    public void load() {

        try {

            view.table()
                    .getItems()
                    .setAll(
                            controller.all()
                    );

        } catch (Exception e) {

            Ui.error(
                    view.root(),
                    e
            );
        }
    }


    public void loadSelected(
            Appointment appointment) {

        if (appointment == null) {
            return;
        }


        selectedId =
                appointment.getAppointmentId();


        if (appointment.getPatient() != null) {

            setPatientOnBoth(
                    appointment.getPatient()
            );
        }


        view.dentist().setValue(
                appointment.getDentist()
        );


        view.treatment().setValue(
                appointment.getTreatment()
        );


        view.date().setValue(
                appointment.getAppointmentDate()
        );


        view.start().setText(
                appointment.getStartTime()
                        .toString()
        );


        view.end().setText(
                appointment.getEndTime()
                        .toString()
        );


        view.status().setValue(
                appointment.getStatus()
        );


        view.remarks().setText(
                appointment.getRemarks() == null
                        ? ""
                        : appointment.getRemarks()
        );


        view.create().setText(
                "Book new"
        );

        refreshValidationMarkers();
    }


    public void create() {

        try {

            /*
             * IMPORTANT:
             *
             * Resolve the patient from both the
             * ComboBox value AND its editor text.
             *
             * This fixes the "Select a valid patient"
             * issue when JavaFX visually shows a patient
             * but getValue() is null.
             */
            Patient patient =
                    resolveEditorSelection(
                            view.patient()
                    );


            if (patient == null) {

                patient =
                        resolveEditorSelection(
                                view.patientSearch()
                        );
            }


            if (patient == null) {

                throw new IllegalArgumentException(
                        "Please select a valid patient."
                );
            }


            /*
             * Put the resolved patient into both
             * ComboBoxes before continuing.
             */
            setPatientOnBoth(patient);


            validate();


            Dentist dentist =
                    view.dentist().getValue();


            Treatment treatment =
                    view.treatment().getValue();


            AppointmentDTO request =
                    new AppointmentDTO(

                            0,

                            null,

                            patient.getPatientId(),

                            dentist.getDentistId(),

                            treatment.getTreatmentId(),

                            view.date().getValue(),

                            LocalTime.parse(
                                    view.start()
                                            .getText()
                                            .trim()
                            ),

                            LocalTime.parse(
                                    view.end()
                                            .getText()
                                            .trim()
                            ),

                            AppointmentStatus.SCHEDULED,

                            view.remarks().getText()
                    );


            AppointmentDTO saved =
                    controller.create(request);


            load();

            clear();


            Ui.notify(
                    view.root(),
                    "Appointment created",
                    saved.getAppointmentNo()
                            + " was booked.",
                    false
            );

        } catch (Exception e) {

            applySubmitError(e);

            Ui.error(
                    view.root(),
                    e
            );
        }
    }


    public void update() {

        try {

            if (selectedId <= 0) {

                throw new IllegalArgumentException(
                        "Select an appointment first."
                );
            }


            /*
             * Resolve patient first.
             */
            Patient patient =
                    resolveEditorSelection(
                            view.patient()
                    );


            if (patient == null) {

                patient =
                        resolveEditorSelection(
                                view.patientSearch()
                        );
            }


            if (patient == null) {

                throw new IllegalArgumentException(
                        "Please select a valid patient."
                );
            }


            setPatientOnBoth(patient);


            validate();


            Dentist dentist =
                    view.dentist().getValue();


            Treatment treatment =
                    view.treatment().getValue();


            AppointmentDTO request =
                    new AppointmentDTO(

                            selectedId,

                            null,

                            patient.getPatientId(),

                            dentist.getDentistId(),

                            treatment.getTreatmentId(),

                            view.date().getValue(),

                            LocalTime.parse(
                                    view.start()
                                            .getText()
                                            .trim()
                            ),

                            LocalTime.parse(
                                    view.end()
                                            .getText()
                                            .trim()
                            ),

                            view.status().getValue(),

                            view.remarks().getText()
                    );


            AppointmentDTO saved =
                    controller.update(request);


            load();


            Ui.notify(
                    view.root(),
                    "Appointment updated",
                    saved.getAppointmentNo()
                            + " was updated.",
                    false
            );

        } catch (Exception e) {

            applySubmitError(e);

            Ui.error(
                    view.root(),
                    e
            );
        }
    }


    private void validate() {

        refreshValidationMarkers();

        /*
         * Patient is already resolved in create/update.
         */
        if (view.patient().getValue() == null) {

            throw new IllegalArgumentException(
                    "Select a patient."
            );
        }


        if (view.dentist().getValue() == null) {

            throw new IllegalArgumentException(
                    "Select a dentist."
            );
        }


        if (view.treatment().getValue() == null) {

            throw new IllegalArgumentException(
                    "Select a treatment."
            );
        }


        if (view.date().getValue() == null) {

            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        if (view.date().getValue().isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Appointment date cannot be in the past."
            );
        }


        Validation.time(
                view.start().getText(),
                "Start time"
        );


        Validation.time(
                view.end().getText(),
                "End time"
        );


        LocalTime startTime =
                LocalTime.parse(
                        view.start()
                                .getText()
                                .trim()
                );


        LocalTime endTime =
                LocalTime.parse(
                        view.end()
                                .getText()
                                .trim()
                );


        if (!endTime.isAfter(startTime)) {

            throw new IllegalArgumentException(
                    "End time must be after start time."
            );
        }
    }


    public void viewPatient() {

        Patient patient =
                resolveEditorSelection(
                        view.patient()
                );


        if (patient == null) {

            patient =
                    resolveEditorSelection(
                            view.patientSearch()
                    );
        }


        if (patient == null) {

            Ui.error(
                    view.root(),
                    new IllegalArgumentException(
                            "Select a patient first."
                    )
            );

            return;
        }


        setPatientOnBoth(patient);


        Ui.showPatientDetails(
                view.root(),
                patient
        );
    }


    public void clear() {

        selectedId = 0;


        syncingPatients = true;

        try {

            view.patient()
                    .setValue(null);

            view.patient()
                    .getEditor()
                    .clear();


            view.patientSearch()
                    .setValue(null);

            view.patientSearch()
                    .getEditor()
                    .clear();

        } finally {

            syncingPatients = false;
        }


        view.patient().setItems(
                FXCollections.observableArrayList(
                        allPatients
                )
        );


        view.patientSearch().setItems(
                FXCollections.observableArrayList(
                        allPatients
                )
        );


        view.dentist().setValue(null);

        view.treatment().setValue(null);

        view.status().setValue(
                AppointmentStatus.SCHEDULED
        );

        view.date().setValue(
                LocalDate.now()
        );

        view.start().setText(
                "09:00"
        );

        view.end().setText(
                "09:30"
        );

        view.remarks().clear();


        view.create().setText(
                "Book appointment"
        );


        view.table()
                .getSelectionModel()
                .clearSelection();

        clearValidationMarkers();
    }


    private void bindRealtimeValidation() {

        view.patient().valueProperty().addListener((obs, old, value) -> refreshValidationMarkers());
        view.dentist().valueProperty().addListener((obs, old, value) -> refreshValidationMarkers());
        view.treatment().valueProperty().addListener((obs, old, value) -> refreshValidationMarkers());
        view.date().valueProperty().addListener((obs, old, value) -> refreshValidationMarkers());
        view.start().textProperty().addListener((obs, old, value) -> refreshValidationMarkers());
        view.end().textProperty().addListener((obs, old, value) -> refreshValidationMarkers());
    }


    private void refreshValidationMarkers() {

        setValidationMessage(
                view.patient(),
                view.patientError(),
                view.patient().getValue() == null
                        ? "Select a patient."
                        : null
        );

        setValidationMessage(
                view.dentist(),
                view.dentistError(),
                view.dentist().getValue() == null
                        ? "Select a dentist."
                        : null
        );

        setValidationMessage(
                view.treatment(),
                view.treatmentError(),
                view.treatment().getValue() == null
                        ? "Select a treatment."
                        : null
        );

        validateDateMarker();
        validateTimeMarkers();
    }


    private void validateDateMarker() {

        if (view.date().getValue() == null) {

            setValidationMessage(
                    view.date(),
                    view.dateError(),
                    "Appointment date is required."
            );

            return;
        }

        if (view.date().getValue().isBefore(LocalDate.now())) {

            setValidationMessage(
                    view.date(),
                    view.dateError(),
                    "Appointment date cannot be in the past."
            );

            return;
        }

        setValidationMessage(
                view.date(),
                view.dateError(),
                null
        );
    }


    private void validateTimeMarkers() {

        boolean startValid = markText(
                () -> Validation.time(
                        view.start().getText(),
                        "Start time"
                ),
                view.start(),
                view.startError()
        );

        boolean endValid = markText(
                () -> Validation.time(
                        view.end().getText(),
                        "End time"
                ),
                view.end(),
                view.endError()
        );

        if (!startValid || !endValid) {
            return;
        }

        LocalTime startTime =
                LocalTime.parse(
                        view.start()
                                .getText()
                                .trim()
                );

        LocalTime endTime =
                LocalTime.parse(
                        view.end()
                                .getText()
                                .trim()
                );

        if (!endTime.isAfter(startTime)) {

            setValidationMessage(
                    view.start(),
                    view.startError(),
                    "Start time must be before end time."
            );

            setValidationMessage(
                    view.end(),
                    view.endError(),
                    "End time must be after start time."
            );
        }
    }


    private boolean markText(Runnable validator, Control control, Label error) {

        try {

            validator.run();
            setValidationMessage(control, error, null);
            return true;

        } catch (IllegalArgumentException ex) {

            setValidationMessage(control, error, ex.getMessage());
            return false;
        }
    }


    private void setValidationMessage(Control control, Label error, String message) {

        boolean invalid =
                message != null
                        && !message.isBlank();

        Validation.markInvalid(control, invalid);

        error.setText(
                invalid
                        ? message
                        : ""
        );

        error.setVisible(invalid);
        error.setManaged(invalid);
    }


    private void clearValidationMarkers() {

        setValidationMessage(view.patient(), view.patientError(), null);
        setValidationMessage(view.dentist(), view.dentistError(), null);
        setValidationMessage(view.treatment(), view.treatmentError(), null);
        setValidationMessage(view.date(), view.dateError(), null);
        setValidationMessage(view.start(), view.startError(), null);
        setValidationMessage(view.end(), view.endError(), null);
    }


    private void applySubmitError(Exception e) {

        String message =
                e.getMessage();

        if (message == null) {
            return;
        }

        String lower =
                message.toLowerCase();

        if (lower.contains("booked")
                || lower.contains("conflict")
                || lower.contains("available slot")) {

            setValidationMessage(
                    view.dentist(),
                    view.dentistError(),
                    message
            );

            setValidationMessage(
                    view.start(),
                    view.startError(),
                    message
            );

            setValidationMessage(
                    view.end(),
                    view.endError(),
                    message
            );
        }
    }
}

package lk.sunrise.dentalclinic.ui.controller;

import javafx.collections.FXCollections;
import javafx.scene.control.TextField;
import lk.sunrise.dentalclinic.controller.AppointmentController;
import lk.sunrise.dentalclinic.controller.TreatmentController;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.ui.util.Ui;
import lk.sunrise.dentalclinic.ui.util.Validation;
import lk.sunrise.dentalclinic.ui.view.TreatmentHistoryView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TreatmentHistoryViewController {

    private final TreatmentHistoryView view;
    private final TreatmentController treatments = new TreatmentController();
    private final AppointmentController appointments = new AppointmentController();

    private List<Appointment> completedAppointments = new ArrayList<>();

    public TreatmentHistoryViewController(TreatmentHistoryView view) {
        this.view = view;
    }

    public void initialize() {
        try {
            loadCompletedAppointments();

            view.search().setOnAction(e -> search());
            view.recordTreatment().setOnAction(e -> record());

            view.appointment().getSelectionModel()
                    .selectedItemProperty()
                    .addListener((obs, old, selected) -> populate(selected));

            TextField editor = view.appointment().getEditor();
            editor.textProperty().addListener((obs, old, value) -> filterAppointments(value));

            view.performedDate().setValue(LocalDate.now());

        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    private void loadCompletedAppointments() throws Exception {
        LocalDate from = LocalDate.now().minusYears(2);
        LocalDate to = LocalDate.now().plusYears(1);

        completedAppointments = appointments.daily(from, to, null)
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .toList();

        view.appointment().setItems(
                FXCollections.observableArrayList(completedAppointments)
        );
    }

    private void filterAppointments(String text) {
        if (text == null) return;

        String q = text.trim().toLowerCase();

        List<Appointment> filtered = completedAppointments.stream()
                .filter(a -> {
                    String patient = a.getPatient() == null
                            ? ""
                            : a.getPatient().getPatientCode() + " " + a.getPatient().getFullName();

                    String treatment = a.getTreatment() == null
                            ? ""
                            : a.getTreatment().getName();

                    String haystack =
                            a.getAppointmentNo()
                                    + " "
                                    + patient
                                    + " "
                                    + treatment
                                    + " "
                                    + a.getAppointmentDate();

                    return haystack.toLowerCase().contains(q);
                })
                .toList();

        view.appointment().setItems(FXCollections.observableArrayList(filtered));

        if (!view.appointment().getEditor().getText().equals(text)) {
            view.appointment().getEditor().setText(text);
        }

        if (!filtered.isEmpty()) {
            view.appointment().show();
        }
    }

    private void populate(Appointment appointment) {
        if (appointment == null) {
            view.patientSummary().setText("Select a completed appointment.");
            view.performedDate().setValue(LocalDate.now());
            view.chargedAmount().clear();
            return;
        }

        Patient p = appointment.getPatient();
        Treatment t = appointment.getTreatment();

        view.patientSummary().setText(
                p == null
                        ? "Patient unavailable"
                        : p.getPatientCode()
                        + "  •  "
                        + p.getFullName()
                        + "  •  Appointment: "
                        + appointment.getAppointmentNo()
        );

        view.patientCode().setText(
                p == null ? "" : p.getPatientCode()
        );

        view.performedDate().setValue(appointment.getAppointmentDate());

        if (t != null && t.getBasePrice() != null) {
            view.chargedAmount().setText(
                    t.getBasePrice().setScale(2).toPlainString()
            );
        }
    }

    private void record() {
        try {
            Appointment appointment = view.appointment().getValue();

            if (appointment == null) {
                throw new IllegalArgumentException(
                        "Select a completed appointment first."
                );
            }

            if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
                throw new IllegalArgumentException(
                        "Only completed appointments can have treatment records."
                );
            }

            if (appointment.getPatient() == null
                    || appointment.getDentist() == null
                    || appointment.getTreatment() == null) {
                throw new IllegalArgumentException(
                        "The appointment is missing patient, dentist or treatment information."
                );
            }

            if (view.performedDate().getValue() == null) {
                throw new IllegalArgumentException(
                        "Performed date is required."
                );
            }

            BigDecimal charged = Validation.decimal(
                    view.chargedAmount().getText(),
                    "Charged amount"
            );

            if (charged.signum() < 0) {
                throw new IllegalArgumentException(
                        "Charged amount cannot be negative."
                );
            }

            List<TreatmentRecord> existing =
                    treatments.getByAppointment(
                            appointment.getAppointmentId()
                    );

            if (!existing.isEmpty()) {
                throw new IllegalArgumentException(
                        "Treatment has already been recorded for this appointment."
                );
            }

            TreatmentRecord record = new TreatmentRecord();
            record.setPatient(appointment.getPatient());
            record.setDentist(appointment.getDentist());
            record.setTreatment(appointment.getTreatment());
            record.setAppointment(appointment);
            record.setPerformedDate(view.performedDate().getValue());
            record.setClinicalNotes(
                    view.clinicalNotes().getText() == null
                            || view.clinicalNotes().getText().isBlank()
                            ? null
                            : view.clinicalNotes().getText().trim()
            );
            record.setChargedAmount(charged);

            if (!treatments.save(record)) {
                throw new IllegalStateException(
                        "Treatment could not be recorded."
                );
            }

            view.patientCode().setText(
                    appointment.getPatient().getPatientCode()
            );

            search();

            Ui.notify(
                    view.root(),
                    "Treatment recorded",
                    "Treatment was recorded successfully. You can now generate the invoice.",
                    false
            );

            view.clinicalNotes().clear();

        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    private void search() {
        try {
            String code = view.patientCode().getText().trim();

            Validation.patientCode(code);

            view.table().getItems().setAll(
                    treatments.getHistory(code)
            );

        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }
}

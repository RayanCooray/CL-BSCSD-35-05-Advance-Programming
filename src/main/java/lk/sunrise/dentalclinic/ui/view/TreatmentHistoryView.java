package lk.sunrise.dentalclinic.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import lk.sunrise.dentalclinic.entity.Appointment;
import lk.sunrise.dentalclinic.entity.TreatmentRecord;
import lk.sunrise.dentalclinic.ui.controller.TreatmentHistoryViewController;
import lk.sunrise.dentalclinic.ui.util.Ui;
import org.controlsfx.control.SearchableComboBox;

public class TreatmentHistoryView {

    private final VBox root = new VBox(18);

    private final TextField patientCode = Ui.textField("Patient code");
    private final Button search = Ui.button("View history", "primary-button");

    private final SearchableComboBox<Appointment> appointment = new SearchableComboBox<>();
    private final Label patientSummary = new Label("Select a completed appointment.");
    private final DatePicker performedDate = new DatePicker();
    private final TextField chargedAmount = Ui.textField("Charged amount");
    private final TextArea clinicalNotes = new TextArea();
    private final Button recordTreatment = Ui.button("Record treatment", "primary-button");

    private final TableView<TreatmentRecord> table = new TableView<>();

    private final TreatmentHistoryViewController controller;

    public TreatmentHistoryView() {
        controller = new TreatmentHistoryViewController(this);

        root.setPadding(new Insets(24));

        Label title = new Label("Patient treatment history");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label(
                "Record completed treatments before generating invoices and review previous treatment records."
        );
        subtitle.getStyleClass().add("page-subtitle");

        HBox searchBar = Ui.row(
                Ui.fieldLabel("Patient"),
                patientCode,
                search
        );

        /*
         * Record treatment form
         */
        GridPane recordGrid = Ui.grid();
        recordGrid.setHgap(18);
        recordGrid.setVgap(14);

        recordGrid.add(Ui.fieldLabel("Completed appointment"), 0, 0);
        recordGrid.add(appointment, 1, 0, 3, 1);

        recordGrid.add(Ui.fieldLabel("Patient"), 0, 1);
        recordGrid.add(patientSummary, 1, 1, 3, 1);
        patientSummary.getStyleClass().add("selection-summary");

        recordGrid.add(Ui.fieldLabel("Performed date"), 0, 2);
        recordGrid.add(performedDate, 1, 2);

        recordGrid.add(Ui.fieldLabel("Charged amount"), 2, 2);
        recordGrid.add(chargedAmount, 3, 2);

        recordGrid.add(Ui.fieldLabel("Clinical notes"), 0, 3);
        clinicalNotes.setPromptText("Treatment notes / remarks");
        clinicalNotes.setWrapText(true);
        clinicalNotes.setPrefRowCount(3);
        recordGrid.add(clinicalNotes, 1, 3, 3, 1);

        recordGrid.add(recordTreatment, 3, 4);

        VBox recordCard = Ui.card("Record treatment");
        recordCard.getChildren().add(recordGrid);

        /*
         * Appointment display
         */
        appointment.setPromptText("Select completed appointment...");
        appointment.setMaxWidth(Double.MAX_VALUE);

        appointment.setConverter(new StringConverter<>() {
            @Override
            public String toString(Appointment a) {
                if (a == null) return "";
                String patient = a.getPatient() == null
                        ? ""
                        : a.getPatient().getPatientCode() + " — " + a.getPatient().getFullName();

                String treatment = a.getTreatment() == null
                        ? ""
                        : a.getTreatment().getName();

                return a.getAppointmentNo()
                        + " — "
                        + patient
                        + " — "
                        + treatment
                        + " — "
                        + a.getAppointmentDate();
            }

            @Override
            public Appointment fromString(String value) {
                return appointment.getValue();
            }
        });

        appointment.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }

                String patient = item.getPatient() == null
                        ? ""
                        : item.getPatient().getPatientCode() + " — " + item.getPatient().getFullName();

                String treatment = item.getTreatment() == null
                        ? ""
                        : item.getTreatment().getName();

                setText(
                        item.getAppointmentNo()
                                + " — "
                                + patient
                                + " — "
                                + treatment
                                + " — "
                                + item.getAppointmentDate()
                );
            }
        });

        appointment.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }

                String patient = item.getPatient() == null
                        ? ""
                        : item.getPatient().getPatientCode() + " — " + item.getPatient().getFullName();

                String treatment = item.getTreatment() == null
                        ? ""
                        : item.getTreatment().getName();

                setText(
                        item.getAppointmentNo()
                                + " — "
                                + patient
                                + " — "
                                + treatment
                                + " — "
                                + item.getAppointmentDate()
                );
            }
        });

        /*
         * History table
         */
        TableColumn<TreatmentRecord, Object> date = column("Date", "performedDate", 130);
        TableColumn<TreatmentRecord, Object> patient = column("Patient", "patient", 190);
        TableColumn<TreatmentRecord, Object> dentist = column("Dentist", "dentist", 170);
        TableColumn<TreatmentRecord, Object> treatment = column("Treatment", "treatment", 190);
        TableColumn<TreatmentRecord, Object> charged = column("Charged", "chargedAmount", 130);
        TableColumn<TreatmentRecord, Object> notes = column("Notes", "clinicalNotes", 260);

        patient.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(((lk.sunrise.dentalclinic.entity.Patient) item).getFullName());
            }
        });

        dentist.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(((lk.sunrise.dentalclinic.entity.Dentist) item).getFullName());
            }
        });

        treatment.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(((lk.sunrise.dentalclinic.entity.Treatment) item).getName());
            }
        });

        charged.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.valueOf(item));
            }
        });

        table.getColumns().addAll(date, patient, dentist, treatment, charged, notes);
        table.setPlaceholder(new Label("Search a patient to view treatment history."));
        Ui.grow(table);

        root.getChildren().addAll(
                title,
                subtitle,
                searchBar,
                recordCard,
                table
        );

        controller.initialize();
    }

    private <T> TableColumn<TreatmentRecord, T> column(
            String title,
            String property,
            double width
    ) {
        TableColumn<TreatmentRecord, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }

    public VBox root() {
        return root;
    }

    public TextField patientCode() {
        return patientCode;
    }

    public Button search() {
        return search;
    }

    public SearchableComboBox<Appointment> appointment() {
        return appointment;
    }

    public Label patientSummary() {
        return patientSummary;
    }

    public DatePicker performedDate() {
        return performedDate;
    }

    public TextField chargedAmount() {
        return chargedAmount;
    }

    public TextArea clinicalNotes() {
        return clinicalNotes;
    }

    public Button recordTreatment() {
        return recordTreatment;
    }

    public TableView<TreatmentRecord> table() {
        return table;
    }
}

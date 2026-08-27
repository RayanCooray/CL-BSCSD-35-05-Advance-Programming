package lk.sunrise.dentalclinic.ui.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.ui.controller.AppointmentViewController;
import lk.sunrise.dentalclinic.ui.util.Ui;

import java.time.LocalDate;

public class AppointmentView {

    private final VBox root = new VBox(18);


    private final ComboBox<Patient> patient = new ComboBox<>();

    private final ComboBox<Patient> patientSearch = new ComboBox<>();

    private final ComboBox<Dentist> dentist = new ComboBox<>();

    private final ComboBox<Treatment> treatment = new ComboBox<>();

    private final DatePicker date =
            new DatePicker(LocalDate.now());

    private final TextField start =
            Ui.textField("09:00");

    private final TextField end =
            Ui.textField("09:30");

    private final TextField remarks =
            Ui.textField("Remarks");

    private final ComboBox<AppointmentStatus> status =
            new ComboBox<>();


    private final Button create =
            Ui.button(
                    "Book appointment",
                    "primary-button"
            );

    private final Button update =
            Ui.button(
                    "Update selected",
                    "secondary-button"
            );

    private final Button clear =
            Ui.button(
                    "Clear",
                    "outline-button"
            );

    private final Button viewPatient =
            Ui.button(
                    "View patient",
                    "outline-button"
            );

    private final Button refresh =
            Ui.button(
                    "Refresh",
                    "secondary-button"
            );


    private final TableView<Appointment> table =
            new TableView<>();


    private final AppointmentViewController controller;


    public AppointmentView() {

        controller =
                new AppointmentViewController(this);

        root.setPadding(
                new Insets(24)
        );

        root.setSpacing(18);


        Label title =
                new Label(
                        "Appointment scheduling"
                );

        title.getStyleClass()
                .add("section-title");

        Label subtitle =
                new Label(
                        "Search and verify the patient before booking. " +
                                "Conflicting dentist bookings are rejected automatically."
                );

        subtitle.getStyleClass()
                .add("page-subtitle");


        GridPane formGrid = Ui.grid();

        formGrid.setHgap(18);
        formGrid.setVgap(18);

        formGrid.setPadding(
                new Insets(
                        8,
                        0,
                        8,
                        0
                )
        );


        ColumnConstraints labelColumn1 =
                new ColumnConstraints();

        labelColumn1.setMinWidth(70);
        labelColumn1.setPrefWidth(80);

        ColumnConstraints fieldColumn1 =
                new ColumnConstraints();

        fieldColumn1.setHgrow(
                Priority.ALWAYS
        );

        fieldColumn1.setFillWidth(true);

        ColumnConstraints labelColumn2 =
                new ColumnConstraints();

        labelColumn2.setMinWidth(70);
        labelColumn2.setPrefWidth(80);

        ColumnConstraints fieldColumn2 =
                new ColumnConstraints();

        fieldColumn2.setHgrow(
                Priority.ALWAYS
        );

        fieldColumn2.setFillWidth(true);

        formGrid.getColumnConstraints()
                .addAll(
                        labelColumn1,
                        fieldColumn1,
                        labelColumn2,
                        fieldColumn2
                );


        add(
                formGrid,
                0,
                "Patient",
                patient,
                0
        );

        add(
                formGrid,
                2,
                "Dentist",
                dentist,
                0
        );

        add(
                formGrid,
                0,
                "Treatment",
                treatment,
                1
        );

        add(
                formGrid,
                2,
                "Date",
                date,
                1
        );

        add(
                formGrid,
                0,
                "Start",
                start,
                2
        );

        add(
                formGrid,
                2,
                "End",
                end,
                2
        );

        add(
                formGrid,
                0,
                "Status",
                status,
                3
        );

        add(
                formGrid,
                2,
                "Remarks",
                remarks,
                3
        );


        HBox actions =
                new HBox(
                        10,
                        create,
                        update,
                        clear
                );

        actions.setAlignment(
                Pos.CENTER_RIGHT
        );

        formGrid.add(
                actions,
                0,
                4,
                4,
                1
        );


        configureControls();


        VBox form =
                Ui.card(
                        "Appointment details"
                );

        form.setPrefHeight(380);
        form.setMinHeight(380);

        form.setSpacing(12);

        form.getChildren()
                .add(formGrid);


        Label selectedLabel =
                Ui.fieldLabel(
                        "Search patient"
                );

        patientSearch.setEditable(true);

        patientSearch.setPromptText(
                "Search patient code / name / contact..."
        );

        patientSearch.setPrefHeight(48);

        patientSearch.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                patientSearch,
                Priority.ALWAYS
        );

        viewPatient.setPrefHeight(48);

        HBox searchBar =
                new HBox(
                        10,
                        selectedLabel,
                        patientSearch,
                        viewPatient
                );

        searchBar.setAlignment(
                Pos.CENTER_LEFT
        );


        Label dateLabel =
                Ui.fieldLabel(
                        "Appointments for"
                );

        date.setPrefHeight(46);

        date.setPrefWidth(220);

        refresh.setPrefHeight(46);

        HBox filterBar =
                new HBox(
                        10,
                        dateLabel,
                        date,
                        refresh
                );

        filterBar.setAlignment(
                Pos.CENTER_LEFT
        );


        table.getColumns().addAll(

                col(
                        "No",
                        "appointmentNo"
                ),

                nested(
                        "Patient",
                        "patient",
                        "fullName"
                ),

                nested(
                        "Patient code",
                        "patient",
                        "patientCode"
                ),

                nested(
                        "Dentist",
                        "dentist",
                        "fullName"
                ),

                nested(
                        "Treatment",
                        "treatment",
                        "name"
                ),

                col(
                        "Date",
                        "appointmentDate"
                ),

                col(
                        "Start",
                        "startTime"
                ),

                col(
                        "End",
                        "endTime"
                ),

                col(
                        "Status",
                        "status"
                )
        );

        table.setPlaceholder(
                new Label(
                        "No appointments found."
                )
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        table.setPrefHeight(300);

        table.setMinHeight(260);

        table.getStyleClass()
                .add("data-table");


        table.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (obs, oldValue, selected) -> {

                            if (selected != null) {

                                controller.loadSelected(
                                        selected
                                );
                            }
                        }
                );

        Ui.grow(table);

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );


        root.getChildren().addAll(
                title,
                subtitle,
                form,
                searchBar,
                filterBar,
                table
        );


        controller.initialize();
    }


    private void configureControls() {


        patient.setEditable(true);

        patient.setPromptText(
                "Search patient code / name..."
        );

        patient.setPrefHeight(48);

        patient.setMaxWidth(
                Double.MAX_VALUE
        );

        configurePatientComboBox(
                patient
        );


        configurePatientComboBox(
                patientSearch
        );

        patientSearch.setEditable(true);

        patientSearch.setPrefHeight(48);

        patientSearch.setMaxWidth(
                Double.MAX_VALUE
        );


        dentist.setPromptText(
                "Select dentist"
        );

        dentist.setPrefHeight(48);

        dentist.setMaxWidth(
                Double.MAX_VALUE
        );

        configureDentistComboBox();


        treatment.setPromptText(
                "Select treatment"
        );

        treatment.setPrefHeight(48);

        treatment.setMaxWidth(
                Double.MAX_VALUE
        );

        configureTreatmentComboBox();


        status.setItems(
                FXCollections.observableArrayList(
                        AppointmentStatus.values()
                )
        );

        status.setValue(
                AppointmentStatus.SCHEDULED
        );

        status.setPrefHeight(48);

        status.setMaxWidth(
                Double.MAX_VALUE
        );


        date.setPromptText(
                "Select date"
        );

        date.setPrefHeight(48);

        date.setMaxWidth(
                Double.MAX_VALUE
        );


        start.setPrefHeight(48);
        end.setPrefHeight(48);
        remarks.setPrefHeight(48);


        patient.setOnAction(
                event ->
                        controller
                                .selectPatientFromMainField()
        );

        patientSearch.setOnAction(
                event ->
                        controller
                                .selectPatientFromSearchField()
        );

        create.setOnAction(
                event ->
                        controller.create()
        );

        update.setOnAction(
                event ->
                        controller.update()
        );

        clear.setOnAction(
                event ->
                        controller.clear()
        );

        refresh.setOnAction(
                event ->
                        controller.load()
        );

        viewPatient.setOnAction(
                event ->
                        controller.viewPatient()
        );
    }


    private void configurePatientComboBox(
            ComboBox<Patient> combo
    ) {

        combo.setConverter(
                new StringConverter<>() {

                    @Override
                    public String toString(
                            Patient value
                    ) {

                        if (value == null) {
                            return "";
                        }

                        return value.getPatientCode()
                                + " — "
                                + value.getFullName()
                                + " — "
                                + value.getContactNumber();
                    }

                    @Override
                    public Patient fromString(
                            String string
                    ) {

                        return null;
                    }
                }
        );


        combo.setCellFactory(
                listView ->
                        new ListCell<>() {

                            @Override
                            protected void updateItem(
                                    Patient item,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                if (
                                        empty ||
                                                item == null
                                ) {

                                    setText(null);

                                } else {

                                    setText(
                                            item.getPatientCode()
                                                    + " — "
                                                    + item.getFullName()
                                                    + " — "
                                                    + item.getContactNumber()
                                    );
                                }
                            }
                        }
        );


        combo.setButtonCell(
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Patient item,
                            boolean empty
                    ) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (
                                empty ||
                                        item == null
                        ) {

                            setText(null);

                        } else {

                            setText(
                                    item.getPatientCode()
                                            + " — "
                                            + item.getFullName()
                                            + " — "
                                            + item.getContactNumber()
                            );
                        }
                    }
                }
        );
    }


    private void configureDentistComboBox() {

        dentist.setConverter(
                new StringConverter<>() {

                    @Override
                    public String toString(
                            Dentist value
                    ) {

                        if (value == null) {
                            return "";
                        }

                        return value.getFullName();
                    }

                    @Override
                    public Dentist fromString(
                            String string
                    ) {

                        return null;
                    }
                }
        );

        dentist.setCellFactory(
                listView ->
                        new ListCell<>() {

                            @Override
                            protected void updateItem(
                                    Dentist item,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                if (
                                        empty ||
                                                item == null
                                ) {

                                    setText(null);

                                } else {

                                    setText(
                                            item.getFullName()
                                    );
                                }
                            }
                        }
        );

        dentist.setButtonCell(
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Dentist item,
                            boolean empty
                    ) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (
                                empty ||
                                        item == null
                        ) {

                            setText(null);

                        } else {

                            setText(
                                    item.getFullName()
                            );
                        }
                    }
                }
        );
    }


    private void configureTreatmentComboBox() {

        treatment.setConverter(
                new StringConverter<>() {

                    @Override
                    public String toString(
                            Treatment value
                    ) {

                        if (value == null) {
                            return "";
                        }

                        return value.getName();
                    }

                    @Override
                    public Treatment fromString(
                            String string
                    ) {

                        return null;
                    }
                }
        );


        treatment.setCellFactory(
                listView ->
                        new ListCell<>() {

                            @Override
                            protected void updateItem(
                                    Treatment item,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                if (
                                        empty ||
                                                item == null
                                ) {

                                    setText(null);

                                } else {

                                    setText(
                                            item.getName()
                                    );
                                }
                            }
                        }
        );


        treatment.setButtonCell(
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Treatment item,
                            boolean empty
                    ) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (
                                empty ||
                                        item == null
                        ) {

                            setText(null);

                        } else {

                            setText(
                                    item.getName()
                            );
                        }
                    }
                }
        );
    }


    private void add(
            GridPane grid,
            int column,
            String label,
            javafx.scene.Node node,
            int row
    ) {

        Label fieldLabel =
                Ui.fieldLabel(label);

        fieldLabel.setMinWidth(65);

        grid.add(
                fieldLabel,
                column,
                row
        );

        grid.add(
                node,
                column + 1,
                row
        );

        GridPane.setHgrow(
                node,
                Priority.ALWAYS
        );

        if (node instanceof Region region) {

            region.setMaxWidth(
                    Double.MAX_VALUE
            );
        }
    }


    private <T> TableColumn<Appointment, T> col(
            String text,
            String property
    ) {

        TableColumn<Appointment, T> column =
                new TableColumn<>(text);

        column.setCellValueFactory(
                new PropertyValueFactory<>(
                        property
                )
        );

        return column;
    }


    private TableColumn<Appointment, String> nested(
            String text,
            String parent,
            String property
    ) {

        TableColumn<Appointment, String> column =
                new TableColumn<>(text);

        column.setCellValueFactory(
                data -> {

                    Appointment appointment =
                            data.getValue();

                    if (appointment == null) {

                        return new SimpleStringProperty(
                                ""
                        );
                    }


                    if ("patient".equals(parent)) {

                        Patient value =
                                appointment.getPatient();

                        if (value == null) {

                            return new SimpleStringProperty(
                                    ""
                            );
                        }

                        if ("fullName".equals(property)) {

                            return new SimpleStringProperty(
                                    value.getFullName()
                            );
                        }

                        if ("patientCode".equals(property)) {

                            return new SimpleStringProperty(
                                    value.getPatientCode()
                            );
                        }
                    }


                    if ("dentist".equals(parent)) {

                        Dentist value =
                                appointment.getDentist();

                        return new SimpleStringProperty(
                                value == null
                                        ? ""
                                        : value.getFullName()
                        );
                    }


                    if ("treatment".equals(parent)) {

                        Treatment value =
                                appointment.getTreatment();

                        return new SimpleStringProperty(
                                value == null
                                        ? ""
                                        : value.getName()
                        );
                    }

                    return new SimpleStringProperty(
                            ""
                    );
                }
        );

        return column;
    }


    public VBox root() {

        return root;
    }

    public ComboBox<Patient> patient() {

        return patient;
    }

    public ComboBox<Patient> patientSearch() {

        return patientSearch;
    }

    public ComboBox<Dentist> dentist() {

        return dentist;
    }

    public ComboBox<Treatment> treatment() {

        return treatment;
    }

    public DatePicker date() {

        return date;
    }

    public TextField start() {

        return start;
    }

    public TextField end() {

        return end;
    }

    public TextField remarks() {

        return remarks;
    }

    public ComboBox<AppointmentStatus> status() {

        return status;
    }

    public Button create() {

        return create;
    }

    public Button update() {

        return update;
    }

    public Button clear() {

        return clear;
    }

    public Button viewPatient() {

        return viewPatient;
    }

    public Button refresh() {

        return refresh;
    }

    public TableView<Appointment> table() {

        return table;
    }

    public AppointmentViewController controller() {

        return controller;
    }
}
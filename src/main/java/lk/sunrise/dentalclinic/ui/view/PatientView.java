package lk.sunrise.dentalclinic.ui.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import lk.sunrise.dentalclinic.entity.Gender;
import lk.sunrise.dentalclinic.entity.Patient;
import lk.sunrise.dentalclinic.ui.controller.PatientViewController;
import lk.sunrise.dentalclinic.ui.util.Ui;

public class PatientView {
    private final VBox root = new VBox(18);
    private final PatientViewController controller;
    private final TextField search = Ui.textField("Search name / code / contact");
    private final TableView<Patient> table = new TableView<>();
    private final TextField name = Ui.textField("Full name");
    private final TextField contact = Ui.textField("Contact number");
    private final TextField email = Ui.textField("Email");
    private final TextField address = Ui.textField("Address");
    private final DatePicker dob = new DatePicker();
    private final ComboBox<Gender> gender = new ComboBox<>();
    private final TextArea history = new TextArea();
    private final Button save = Ui.button("Register patient", "primary-button");
    private final Button update = Ui.button("Update selected", "secondary-button");
    private final Button clear = Ui.button("Clear", "outline-button");
    private final Button viewDetails = Ui.button("View details", "outline-button");
    private final Label mode = new Label("New patient");

    public PatientView() {
        controller = new PatientViewController(this);
        root.setPadding(new Insets(24));
        root.setFillWidth(true);

        Label title = new Label("Patient registration");
        title.getStyleClass().add("section-title");
        Label sub = new Label("Register, search, verify and update patient records.");
        sub.getStyleClass().add("page-subtitle");

        mode.getStyleClass().add("form-mode");
        HBox formHeader = new HBox(10, new Label("Patient profile"), mode);
        formHeader.setAlignment(Pos.CENTER_LEFT);
        ((Label) formHeader.getChildren().get(0)).getStyleClass().add("section-title");

        GridPane g = Ui.grid();
        g.setHgap(18);
        g.setVgap(14);
        addField(g, 0, 0, "Full name", name);
        addField(g, 2, 0, "Date of birth", dob);
        addField(g, 0, 1, "Gender", gender);
        addField(g, 2, 1, "Contact", contact);
        addField(g, 0, 2, "Email", email);
        addField(g, 2, 2, "Address", address);
        history.setPromptText("Medical history");
        history.setPrefRowCount(4);
        history.setWrapText(true);
        g.add(Ui.fieldLabel("Medical history"), 0, 3);
        g.add(history, 1, 3, 3, 1);
        GridPane.setHgrow(history, Priority.ALWAYS);

        HBox actions = new HBox(10, save, update, clear);
        actions.setAlignment(Pos.CENTER_RIGHT);
        g.add(actions, 0, 4, 4, 1);

        gender.setItems(FXCollections.observableArrayList(Gender.values()));
        gender.setPromptText("Select gender");
        dob.setPromptText("Select date");

        VBox form = new VBox(14);
        form.getStyleClass().add("card");
        form.getChildren().addAll(formHeader, g);

        Button searchButton = Ui.button("Search", "secondary-button");
        HBox searchBar = new HBox(10, search, searchButton, viewDetails);
        search.setPrefWidth(350);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchButton.setOnAction(e -> controller.search());
        viewDetails.setOnAction(e -> controller.viewSelected());

        table.getColumns().addAll(
                col("Code", "patientCode"), col("Name", "fullName"), col("DOB", "dateOfBirth"),
                col("Gender", "gender"), col("Contact", "contactNumber"), col("Email", "email")
        );
        table.setPlaceholder(new Label("No patient records found."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> controller.loadSelected(selected));
        table.setRowFactory(tv -> {
            TableRow<Patient> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getClickCount() == 2) controller.viewSelected();
            });
            return row;
        });
        Ui.grow(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        root.getChildren().addAll(title, sub, form, searchBar, table);
        controller.initialize();
    }

    private void addField(GridPane g, int c, int r, String label, javafx.scene.Node node) {
        g.add(Ui.fieldLabel(label), c, r);
        g.add(node, c + 1, r);
        GridPane.setHgrow(node, Priority.ALWAYS);
        if (node instanceof Region region) region.setMaxWidth(Double.MAX_VALUE);
    }

    private <T> TableColumn<Patient, T> col(String text, String property) {
        TableColumn<Patient, T> c = new TableColumn<>(text);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }

    public VBox root() {
        return root;
    }

    public TextField search() {
        return search;
    }

    public TextField name() {
        return name;
    }

    public TextField contact() {
        return contact;
    }

    public TextField email() {
        return email;
    }

    public TextField address() {
        return address;
    }

    public DatePicker dob() {
        return dob;
    }

    public ComboBox<Gender> gender() {
        return gender;
    }

    public TextArea history() {
        return history;
    }

    public Button save() {
        return save;
    }

    public Button update() {
        return update;
    }

    public Button clear() {
        return clear;
    }

    public Button viewDetails() {
        return viewDetails;
    }

    public TableView<Patient> table() {
        return table;
    }

    public Label mode() {
        return mode;
    }
}

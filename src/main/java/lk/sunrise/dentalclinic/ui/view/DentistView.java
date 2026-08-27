package lk.sunrise.dentalclinic.ui.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import lk.sunrise.dentalclinic.dto.DentistDTO;
import lk.sunrise.dentalclinic.entity.Dentist;
import lk.sunrise.dentalclinic.ui.controller.DentistViewController;
import lk.sunrise.dentalclinic.ui.util.Ui;

import java.math.BigDecimal;
import java.time.LocalTime;

public class DentistView {
    private final VBox root = new VBox(18);
    private final TextField search = Ui.textField("Search dentist / SLMC / code");
    private final TableView<Dentist> table = new TableView<>();
    private final TextField name = Ui.textField("Full name"), slmc = Ui.textField("SLMC number"), special = Ui.textField("Specialization"), contact = Ui.textField("Contact"), email = Ui.textField("Email"), fee = Ui.textField("Consultation fee");
    private final TextField start = Ui.textField("09:00"), end = Ui.textField("17:00");
    private final CheckBox available = new CheckBox("Available");
    private final Button save = Ui.button("Add dentist", "primary-button"), update = Ui.button("Update selected", "secondary-button");
    private final DentistViewController controller;

    public DentistView() {
        controller = new DentistViewController(this);
        root.setPadding(new Insets(24));
        Label t = new Label("Dentist management");
        t.getStyleClass().add("section-title");
        Label s = new Label("Maintain dentist profiles, consultation fees and working hours.");
        s.getStyleClass().add("page-subtitle");
        GridPane g = Ui.grid();
        field(g, 0, "Name", name, 0);
        field(g, 2, "SLMC", slmc, 0);
        field(g, 0, "Specialization", special, 1);
        field(g, 2, "Contact", contact, 1);
        field(g, 0, "Email", email, 2);
        field(g, 2, "Fee", fee, 2);
        field(g, 0, "Start", start, 3);
        field(g, 2, "End", end, 3);
        g.add(available, 1, 4);
        g.add(save, 2, 4);
        g.add(update, 3, 4);
        VBox form = Ui.card("Dentist profile");
        form.getChildren().add(g);
        HBox sb = Ui.row(search, Ui.button("Search", "secondary-button"));
        ((Button) sb.getChildren().get(1)).setOnAction(e -> controller.search());
        table.getColumns().addAll(col("Code", "dentistCode"), col("Name", "fullName"), col("SLMC", "slmcNumber"), col("Specialization", "specialization"), col("Fee", "consultationFee"), col("Available", "available"));
        table.setPlaceholder(new Label("No dentists found."));
        Ui.grow(table);
        table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) controller.loadSelected();
        });
        root.getChildren().addAll(t, s, form, sb, table);
        controller.initialize();
    }

    private void field(GridPane g, int c, String l, javafx.scene.Node n, int r) {
        g.add(Ui.fieldLabel(l), c, r);
        g.add(n, c + 1, r);
    }

    private <T> TableColumn<Dentist, T> col(String x, String p) {
        TableColumn<Dentist, T> c = new TableColumn<>(x);
        c.setCellValueFactory(new PropertyValueFactory<>(p));
        c.setPrefWidth(150);
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

    public TextField slmc() {
        return slmc;
    }

    public TextField special() {
        return special;
    }

    public TextField contact() {
        return contact;
    }

    public TextField email() {
        return email;
    }

    public TextField fee() {
        return fee;
    }

    public TextField start() {
        return start;
    }

    public TextField end() {
        return end;
    }

    public CheckBox available() {
        return available;
    }

    public Button save() {
        return save;
    }

    public Button update() {
        return update;
    }

    public TableView<Dentist> table() {
        return table;
    }
}

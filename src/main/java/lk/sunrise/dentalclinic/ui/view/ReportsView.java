package lk.sunrise.dentalclinic.ui.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.Appointment;
import lk.sunrise.dentalclinic.ui.controller.ReportsViewController;
import lk.sunrise.dentalclinic.ui.util.Ui;

import java.time.LocalDate;

public class ReportsView {
    private final VBox root = new VBox(18);
    private final DatePicker from = new DatePicker(LocalDate.now()), to = new DatePicker(LocalDate.now());
    private final ComboBox<String> format = new ComboBox<>();
    private final Button daily = Ui.button("Daily appointments", "primary-button");
    private final Spinner<Integer> year = new Spinner<>(2020, 2100, LocalDate.now().getYear());
    private final Spinner<Integer> month = new Spinner<>(1, 12, LocalDate.now().getMonthValue());
    private final Button monthly = Ui.button("Monthly revenue", "secondary-button");
    private final Label summary = new Label();
    private final TableView<Appointment> table = new TableView<>();
    private final ReportsViewController controller;

    public ReportsView() {
        controller = new ReportsViewController(this);
        root.setPadding(new Insets(24));
        Label t = new Label("Reports");
        t.getStyleClass().add("section-title");
        Label s = new Label("Run live appointment and revenue reports from the MySQL database.");
        s.getStyleClass().add("page-subtitle");
        GridPane g = Ui.grid();
        g.add(Ui.fieldLabel("From"), 0, 0);
        g.add(from, 1, 0);
        g.add(Ui.fieldLabel("To"), 2, 0);
        g.add(to, 3, 0);
        g.add(Ui.fieldLabel("Output"), 0, 1);
        g.add(format, 1, 1);
        g.add(daily, 3, 1);
        VBox d = Ui.card("Daily appointment report");
        d.getChildren().add(g);
        GridPane m = Ui.grid();
        m.add(Ui.fieldLabel("Year"), 0, 0);
        m.add(year, 1, 0);
        m.add(Ui.fieldLabel("Month"), 2, 0);
        m.add(month, 3, 0);
        m.add(monthly, 3, 1);
        VBox r = Ui.card("Monthly revenue report");
        r.getChildren().add(m);
        summary.getStyleClass().add("label-muted");
        table.getColumns().addAll(col("No", "appointmentNo"), col("Date", "appointmentDate"), col("Start", "startTime"), col("Status", "status"));
        table.setPlaceholder(new Label("Run a daily report to populate results."));
        Ui.grow(table);
        root.getChildren().addAll(t, s, d, r, summary, table);
        controller.initialize();
    }

    private <T> TableColumn<Appointment, T> col(String x, String p) {
        TableColumn<Appointment, T> c = new TableColumn<>(x);
        c.setCellValueFactory(new PropertyValueFactory<>(p));
        c.setPrefWidth(180);
        return c;
    }

    public VBox root() {
        return root;
    }

    public DatePicker from() {
        return from;
    }

    public DatePicker to() {
        return to;
    }

    public ComboBox<String> format() {
        return format;
    }

    public Button daily() {
        return daily;
    }

    public Spinner<Integer> year() {
        return year;
    }

    public Spinner<Integer> month() {
        return month;
    }

    public Button monthly() {
        return monthly;
    }

    public Label summary() {
        return summary;
    }

    public TableView<Appointment> table() {
        return table;
    }
}

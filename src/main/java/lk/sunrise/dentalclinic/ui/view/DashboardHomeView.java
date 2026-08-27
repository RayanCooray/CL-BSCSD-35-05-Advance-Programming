package lk.sunrise.dentalclinic.ui.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.layout.*;
import lk.sunrise.dentalclinic.controller.PatientController;
import lk.sunrise.dentalclinic.controller.ReportController;
import lk.sunrise.dentalclinic.dto.ReportRequestDTO;
import lk.sunrise.dentalclinic.dto.RevenueReportDTO;
import lk.sunrise.dentalclinic.entity.Appointment;
import lk.sunrise.dentalclinic.entity.AppointmentStatus;
import lk.sunrise.dentalclinic.ui.session.SessionContext;
import lk.sunrise.dentalclinic.ui.util.Ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DashboardHomeView {
    private final VBox root = new VBox(20);
    private final PatientController patients = new PatientController();
    private final ReportController reports = new ReportController();

    public DashboardHomeView() {
        root.setPadding(new Insets(24));
        root.setFillWidth(true);
        Label title = new Label("Good day, " + SessionContext.getInstance().getFullName());
        title.getStyleClass().add("page-title");
        Label sub = new Label("Live clinic overview. All metrics are loaded from the configured MySQL database.");
        sub.getStyleClass().add("page-subtitle");

        HBox metrics = new HBox(14);
        metrics.setMaxWidth(Double.MAX_VALUE);
        try {
            int patientCount = patients.search("").size();
            List<Appointment> today = reports.dailyAppointments(new ReportRequestDTO(LocalDate.now(), LocalDate.now(), null, "SCREEN"));
            long completed = today.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
            RevenueReportDTO revenue = reports.monthlyRevenue(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
            metrics.getChildren().addAll(metric("Patients", String.valueOf(patientCount), "Total registered", "patients"), metric("Today", String.valueOf(today.size()), "Appointments", "calendar"), metric("Completed", String.valueOf(completed), "Today's completed", "check"), metric("Revenue", "LKR " + money(revenue.getRevenue()), "Current month", "money"));
        } catch (Exception e) {
            metrics.getChildren().addAll(metric("Patients", "—", "Database unavailable", "patients"), metric("Appointments", "—", "Database unavailable", "calendar"), metric("Completed", "—", "Database unavailable", "check"), metric("Revenue", "—", "Database unavailable", "money"));
        }
        for (Node n : metrics.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        PieChart statusChart = new PieChart();
        statusChart.setTitle("Today's appointment status");
        statusChart.setLegendVisible(true);
        statusChart.setPrefHeight(310);
        BarChart<String, Number> revenueChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        revenueChart.setTitle("Monthly revenue snapshot");
        revenueChart.setLegendVisible(false);
        revenueChart.setPrefHeight(310);
        loadCharts(statusChart, revenueChart);

        HBox charts = new HBox(16, chartCard("Appointment overview", statusChart), chartCard("Revenue", revenueChart));
        HBox.setHgrow(charts.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(charts.getChildren().get(1), Priority.ALWAYS);

        TableView<Appointment> recent = new TableView<>();
        recent.getColumns().addAll(simple("No", a -> a.getAppointmentNo()), simple("Patient", a -> a.getPatient() == null ? "" : a.getPatient().getFullName()), simple("Dentist", a -> a.getDentist() == null ? "" : a.getDentist().getFullName()), simple("Time", a -> a.getStartTime() == null ? "" : a.getStartTime().toString()), simple("Status", a -> a.getStatus() == null ? "" : a.getStatus().name()));
        recent.setPlaceholder(new Label("No appointments for today."));
        recent.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        recent.getStyleClass().add("dashboard-table");
        try {
            recent.getItems().setAll(reports.dailyAppointments(new ReportRequestDTO(LocalDate.now(), LocalDate.now(), null, "SCREEN")));
        } catch (Exception ignored) {
        }
        VBox recentCard = Ui.card("Today's schedule");
        recentCard.getChildren().add(recent);
        VBox.setVgrow(recent, Priority.ALWAYS);

        root.getChildren().addAll(title, sub, metrics, charts, recentCard);
    }

    private VBox metric(String title, String value, String caption, String icon) {
        VBox b = new VBox(8);
        b.getStyleClass().add("metric-card");
        HBox top = new HBox(10, Ui.icon(icon.equals("patients") ? org.kordamp.ikonli.fontawesome5.FontAwesomeSolid.USERS : icon.equals("calendar") ? org.kordamp.ikonli.fontawesome5.FontAwesomeSolid.CALENDAR_ALT : icon.equals("check") ? org.kordamp.ikonli.fontawesome5.FontAwesomeSolid.CHECK_CIRCLE : org.kordamp.ikonli.fontawesome5.FontAwesomeSolid.MONEY_BILL, 18), new Label(title));
        top.setAlignment(Pos.CENTER_LEFT);
        ((Label) top.getChildren().get(1)).getStyleClass().add("metric-label");
        Label v = new Label(value);
        v.getStyleClass().add("metric-value");
        Label c = new Label(caption);
        c.getStyleClass().add("label-muted");
        b.getChildren().addAll(top, v, c);
        return b;
    }

    private VBox chartCard(String title, Node chart) {
        VBox box = Ui.card(title);
        box.getChildren().add(chart);
        VBox.setVgrow(chart, Priority.ALWAYS);
        return box;
    }

    private void loadCharts(PieChart statusChart, BarChart<String, Number> revenueChart) {
        try {
            List<Appointment> today = reports.dailyAppointments(new ReportRequestDTO(LocalDate.now(), LocalDate.now(), null, "SCREEN"));
            for (AppointmentStatus s : AppointmentStatus.values()) {
                long count = today.stream().filter(a -> a.getStatus() == s).count();
                if (count > 0) statusChart.getData().add(new PieChart.Data(s.name(), count));
            }
            RevenueReportDTO r = reports.monthlyRevenue(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>(LocalDate.now().getMonth().name(), r.getRevenue()));
            revenueChart.getData().add(series);
        } catch (Exception ignored) {
        }
    }

    private <T> TableColumn<Appointment, T> simple(String title, java.util.function.Function<Appointment, T> fn) {
        TableColumn<Appointment, T> c = new TableColumn<>(title);
        c.setCellValueFactory(v -> new javafx.beans.property.SimpleObjectProperty<>(fn.apply(v.getValue())));
        return c;
    }

    private String money(BigDecimal v) {
        return v == null ? "0.00" : v.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    public VBox root() {
        return root;
    }
}

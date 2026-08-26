package lk.sunrise.dentalclinic.ui.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lk.sunrise.dentalclinic.controller.AuthController;
import lk.sunrise.dentalclinic.ui.Navigation;
import lk.sunrise.dentalclinic.ui.session.SessionContext;
import lk.sunrise.dentalclinic.ui.util.Ui;
import lk.sunrise.dentalclinic.ui.view.*;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class DashboardViewController {
    private final DashboardView view;
    private final SessionContext session = SessionContext.getInstance();
    private final AuthController auth = new AuthController();

    public DashboardViewController(DashboardView view) { this.view = view; }

    public void initialize() {
        view.userLabel().setText(session.getFullName() + "  •  " + session.getRole());
        add("Dashboard", "dashboard", FontAwesomeSolid.HOME);
        add("Patients", "patients", FontAwesomeSolid.USERS);
        if (view.canAccess("dentists")) add("Dentists", "dentists", FontAwesomeSolid.USER_MD);
        if (view.canAccess("treatments")) add("Treatments", "treatments", FontAwesomeSolid.TOOTH);
        if (view.canAccess("appointments")) add("Appointments", "appointments", FontAwesomeSolid.CALENDAR_ALT);
        if (view.canAccess("history")) add("Treatment History", "history", FontAwesomeSolid.LIST_ALT);
        if (view.canAccess("billing")) add("Billing", "billing", FontAwesomeSolid.MONEY_BILL);
        if (view.canAccess("reports")) add("Reports", "reports", FontAwesomeSolid.CHART_BAR);
        if (view.canAccess("users")) add("Users", "users", FontAwesomeSolid.USER);
        show("dashboard");
    }

    private void add(String text, String key, FontAwesomeSolid icon) {
        Button b = new Button(text, Ui.icon(icon, 15));
        b.getStyleClass().add("nav-button"); b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> show(key));
        view.nav().getChildren().add(b);
    }

    public void show(String key) {
        if (!view.canAccess(key)) return;
        view.content().getChildren().clear();
        view.nav().getChildren().forEach(n -> n.getStyleClass().remove("active"));
        for (var n : view.nav().getChildren()) {
            if (n instanceof Button b && b.getText().equalsIgnoreCase(labelFor(key))) b.getStyleClass().add("active");
        }
        switch (key) {
            case "patients" -> { view.pageTitle().setText("Patients"); view.content().getChildren().add(new PatientView().root()); }
            case "dentists" -> { view.pageTitle().setText("Dentists"); view.content().getChildren().add(new DentistView().root()); }
            case "treatments" -> { view.pageTitle().setText("Treatments"); view.content().getChildren().add(new TreatmentView().root()); }
            case "appointments" -> { view.pageTitle().setText("Appointments"); view.content().getChildren().add(new AppointmentView().root()); }
            case "history" -> { view.pageTitle().setText("Treatment History"); view.content().getChildren().add(new TreatmentHistoryView().root()); }
            case "billing" -> { view.pageTitle().setText("Billing & Payments"); view.content().getChildren().add(new BillingView().root()); }
            case "reports" -> { view.pageTitle().setText("Reports"); view.content().getChildren().add(new ReportsView().root()); }
            case "users" -> { view.pageTitle().setText("User Management"); view.content().getChildren().add(new UserManagementView().root()); }
            default -> { view.pageTitle().setText("Dashboard"); view.content().getChildren().add(new DashboardHomeView().root()); }
        }
    }

    private String labelFor(String key) {
        return switch (key) {
            case "patients" -> "Patients"; case "dentists" -> "Dentists"; case "treatments" -> "Treatments";
            case "appointments" -> "Appointments"; case "history" -> "Treatment History"; case "billing" -> "Billing";
            case "reports" -> "Reports"; case "users" -> "Users"; default -> "Dashboard";
        };
    }

    public void logout() {
        try { if (session.getToken() != null) auth.logout(session.getToken()); } catch (Exception ignored) {}
        session.clear(); Navigation.showLogin();
    }
}

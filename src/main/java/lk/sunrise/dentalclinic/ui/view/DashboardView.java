package lk.sunrise.dentalclinic.ui.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lk.sunrise.dentalclinic.entity.UserRole;
import lk.sunrise.dentalclinic.ui.controller.DashboardViewController;
import lk.sunrise.dentalclinic.ui.session.SessionContext;

public class DashboardView {
    private final BorderPane root = new BorderPane();
    private final VBox nav = new VBox(7);
    private final StackPane content = new StackPane();
    private final Label pageTitle = new Label("Dashboard");
    private final Label userLabel = new Label();
    private final DashboardViewController controller;
    public DashboardView() {
        root.getStyleClass().add("app-shell");
        root.setLeft(buildSidebar()); root.setTop(buildTopbar());
        content.setPadding(new Insets(0)); root.setCenter(content);
        controller = new DashboardViewController(this); controller.initialize();
    }
    private VBox buildSidebar() {
        VBox side = new VBox(12); side.getStyleClass().add("sidebar"); side.setPrefWidth(235);
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/lk/sunrise/dentalclinic/ui/clinic-logo.png")));
        logo.setFitWidth(58); logo.setFitHeight(58); logo.setPreserveRatio(true);
        Label brand = new Label("SUNRISE DENTAL"); brand.getStyleClass().add("brand");
        Label sub = new Label("CLINIC MANAGEMENT"); sub.getStyleClass().add("brand-sub");
        nav.setPadding(new Insets(15,0,0,0));
        HBox brandRow = new HBox(10, logo, new VBox(2, brand, sub));
        brandRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        side.getChildren().addAll(brandRow, nav); VBox.setVgrow(nav, Priority.ALWAYS);
        Button logout = new Button("Sign out", lk.sunrise.dentalclinic.ui.util.Ui.icon(org.kordamp.ikonli.fontawesome5.FontAwesomeSolid.SIGN_OUT_ALT, 15)); logout.getStyleClass().add("logout-button"); logout.setMaxWidth(Double.MAX_VALUE); logout.setOnAction(e -> controller.logout()); side.getChildren().add(logout);
        return side;
    }
    private HBox buildTopbar() {
        HBox bar = new HBox(15); bar.getStyleClass().add("topbar");
        pageTitle.getStyleClass().add("page-title"); userLabel.getStyleClass().add("role-pill");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS); bar.getChildren().addAll(pageTitle, spacer, userLabel); return bar;
    }
    public Scene scene() { Scene s = new Scene(root, 1280, 820); s.getStylesheets().add(getClass().getResource("/lk/sunrise/dentalclinic/ui/app.css").toExternalForm()); return s; }
    public VBox nav() { return nav; } public StackPane content() { return content; } public Label pageTitle() { return pageTitle; } public Label userLabel() { return userLabel; }
    public boolean canAccess(String key) {
        UserRole r = SessionContext.getInstance().getRole();
        return switch (key) {
            case "dashboard" -> r != null;
            case "patients", "appointments", "billing" -> r == UserRole.ADMIN || r == UserRole.RECEPTIONIST || r == UserRole.DENTIST;
            case "dentists", "treatments" -> r == UserRole.ADMIN;
            case "history" -> r == UserRole.ADMIN || r == UserRole.DENTIST;
            case "reports" -> r == UserRole.ADMIN || r == UserRole.MANAGEMENT;
            case "users" -> r == UserRole.ADMIN;
            default -> false;
        };
    }
}

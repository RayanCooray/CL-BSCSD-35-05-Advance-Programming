package lk.sunrise.dentalclinic.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.sunrise.dentalclinic.ui.view.DashboardView;
import lk.sunrise.dentalclinic.ui.view.LoginView;

public final class Navigation {
    private static Stage stage;
    private Navigation() {}
    public static void init(Stage primaryStage) { stage = primaryStage; }
    public static Stage stage() { return stage; }
    public static void showLogin() { setScene(new LoginView().scene()); }
    public static void showDashboard() { setScene(new DashboardView().scene()); }
    private static void setScene(Scene scene) {
        stage.setScene(scene); stage.setMinWidth(1100); stage.setMinHeight(720); stage.centerOnScreen(); stage.show();
    }
}

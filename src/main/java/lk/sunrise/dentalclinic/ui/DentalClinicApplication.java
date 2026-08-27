package lk.sunrise.dentalclinic.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.sunrise.dentalclinic.ui.view.LoginView;

public class DentalClinicApplication extends Application {
    @Override public void start(Stage stage) {
        Navigation.init(stage);
        stage.setTitle("Sunrise Dental Clinic Management System");
        Scene scene = new LoginView().scene();
        stage.setScene(scene); stage.setMinWidth(1100); stage.setMinHeight(720); stage.show();
    }
    public static void main(String[] args) { launch(args); }
}

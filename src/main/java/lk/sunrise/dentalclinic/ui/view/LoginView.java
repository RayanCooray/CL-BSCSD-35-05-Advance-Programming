package lk.sunrise.dentalclinic.ui.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lk.sunrise.dentalclinic.ui.controller.LoginViewController;
import lk.sunrise.dentalclinic.ui.util.Ui;

public class LoginView {
    private final StackPane root = new StackPane();
    private final TextField username = Ui.textField("Username");
    private final PasswordField password = Ui.passwordField("Password");
    private final Label error = new Label();
    private final Button login = Ui.button("Sign in", "primary-button");

    public LoginView() {
        root.getStyleClass().add("login-bg");
        VBox card = new VBox(14); card.getStyleClass().add("login-card"); card.setMaxWidth(430); card.setMaxHeight(520);
        Label brand = new Label("SUNRISE DENTAL"); brand.getStyleClass().add("login-accent");
        Label title = new Label("Welcome back"); title.getStyleClass().add("login-title");
        Label sub = new Label("Sign in with a clinic account. No demo credentials are used."); sub.getStyleClass().add("label-muted"); sub.setWrapText(true);
        username.setPrefHeight(44); password.setPrefHeight(44); login.setPrefHeight(44); login.setMaxWidth(Double.MAX_VALUE);
        error.getStyleClass().add("error-text");
        card.getChildren().addAll(brand, title, sub, Ui.fieldLabel("Username"), username, Ui.fieldLabel("Password"), password, login, error);
        root.getChildren().add(card); StackPane.setAlignment(card, Pos.CENTER);
        new LoginViewController(this).initialize();
    }
    public Scene scene() { Scene s = new Scene(root, 1200, 760); s.getStylesheets().add(getClass().getResource("/lk/sunrise/dentalclinic/ui/app.css").toExternalForm()); return s; }
    public TextField usernameField() { return username; }
    public PasswordField passwordField() { return password; }
    public Button loginButton() { return login; }
    public Label errorLabel() { return error; }
    public StackPane root() { return root; }
}

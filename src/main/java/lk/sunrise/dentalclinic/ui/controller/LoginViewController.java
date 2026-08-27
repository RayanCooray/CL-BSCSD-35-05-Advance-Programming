package lk.sunrise.dentalclinic.ui.controller;

import lk.sunrise.dentalclinic.controller.AuthController;
import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.ui.Navigation;
import lk.sunrise.dentalclinic.ui.session.SessionContext;
import lk.sunrise.dentalclinic.ui.view.LoginView;

public class LoginViewController {
    private final LoginView view;
    private final AuthController auth = new AuthController();

    public LoginViewController(LoginView view) {
        this.view = view;
    }

    public void initialize() {
        view.loginButton().setOnAction(e -> login());
        view.passwordField().setOnAction(e -> login());
    }

    private void login() {
        view.errorLabel().setText("");
        try {
            LoginResponseDTO response = auth.login(new LoginRequestDTO(view.usernameField().getText(), view.passwordField().getText()));
            SessionContext.getInstance().start(response);
            Navigation.showDashboard();
        } catch (Exception ex) {
            view.errorLabel().setText(ex.getMessage() == null ? "Invalid username or password." : ex.getMessage());
        }
    }
}

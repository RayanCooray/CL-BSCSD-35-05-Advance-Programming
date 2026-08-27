package lk.sunrise.dentalclinic.ui.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lk.sunrise.dentalclinic.controller.AuthController;
import lk.sunrise.dentalclinic.entity.UserRole;
import lk.sunrise.dentalclinic.ui.util.Ui;

public class UserManagementView {
    private final VBox root = new VBox(18);
    private final TextField username = Ui.textField("Username"), fullName = Ui.textField("Full name"), email = Ui.textField("Email");
    private final PasswordField password = Ui.passwordField("Password");
    private final ComboBox<UserRole> role = new ComboBox<>();
    private final Button save = Ui.button("Create user", "primary-button");

    public UserManagementView() {
        root.setPadding(new Insets(24));
        Label t = new Label("User management");
        t.getStyleClass().add("section-title");
        Label s = new Label("Create real clinic accounts. No default or demo credentials are inserted by the UI.");
        s.getStyleClass().add("page-subtitle");
        GridPane g = Ui.grid();
        f(g, 0, "Username", username, 0);
        f(g, 2, "Full name", fullName, 0);
        f(g, 0, "Password", password, 1);
        f(g, 2, "Email", email, 1);
        f(g, 0, "Role", role, 2);
        g.add(save, 3, 2);
        role.setItems(FXCollections.observableArrayList(UserRole.values()));
        VBox card = Ui.card("Create account");
        card.getChildren().add(g);
        root.getChildren().addAll(t, s, card);
        save.setOnAction(e -> create());
    }

    private void f(GridPane g, int c, String l, javafx.scene.Node n, int r) {
        g.add(Ui.fieldLabel(l), c, r);
        g.add(n, c + 1, r);
    }

    private void create() {
        try {
            if (new AuthController().register(username.getText(), password.getText(), fullName.getText(), email.getText(), role.getValue())) {
                Ui.notify(root, "User created", "The account was saved to MySQL.", false);
                username.clear();
                password.clear();
                fullName.clear();
                email.clear();
                role.setValue(null);
            }
        } catch (Exception e) {
            Ui.error(root, e);
        }
    }

    public VBox root() {
        return root;
    }
}

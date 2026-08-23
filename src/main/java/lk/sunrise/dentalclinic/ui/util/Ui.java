package lk.sunrise.dentalclinic.ui.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.Notifications;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import lk.sunrise.dentalclinic.entity.Patient;

public final class Ui {
    private Ui() {}

    public static VBox page(String title, String subtitle) {
        VBox box = new VBox(18);
        box.setPadding(new Insets(24));
        Label t = new Label(title); t.getStyleClass().add("page-title");
        Label s = new Label(subtitle); s.getStyleClass().add("page-subtitle");
        box.getChildren().addAll(t, s);
        return box;
    }

    public static VBox card(String title) {
        VBox box = new VBox(14); box.getStyleClass().add("card");
        Label label = new Label(title); label.getStyleClass().add("section-title");
        box.getChildren().add(label);
        return box;
    }

    public static Label fieldLabel(String text) {
        Label l = new Label(text); l.getStyleClass().add("form-label"); return l;
    }

    public static TextField textField(String prompt) {
        TextField f = new TextField(); f.setPromptText(prompt); f.getStyleClass().add("text-field"); return f;
    }

    public static PasswordField passwordField(String prompt) {
        PasswordField f = new PasswordField(); f.setPromptText(prompt); f.getStyleClass().add("password-field"); return f;
    }

    public static Button button(String text, String css) {
        Button b = new Button(text); b.getStyleClass().add(css); return b;
    }

    public static Button iconButton(String tooltip, FontAwesomeSolid icon, String css) {
        Button b = new Button();
        FontIcon i = new FontIcon(icon);
        i.setIconSize(15);
        b.setGraphic(i);
        b.getStyleClass().add(css);
        b.setTooltip(new Tooltip(tooltip));
        return b;
    }

    public static FontIcon icon(FontAwesomeSolid icon, int size) {
        FontIcon i = new FontIcon(icon);
        i.setIconSize(size);
        return i;
    }

    public static HBox row(Node... nodes) { HBox h = new HBox(10, nodes); h.setFillHeight(true); return h; }
    public static GridPane grid() { GridPane g = new GridPane(); g.setHgap(12); g.setVgap(10); return g; }
    public static void grow(Node n) { VBox.setVgrow(n, Priority.ALWAYS); HBox.setHgrow(n, Priority.ALWAYS); }

    public static void notify(Node owner, String title, String text, boolean error) {
        Notifications.create().title(title).text(text).owner(owner)
                .hideAfter(javafx.util.Duration.seconds(3)).show();
    }

    public static void error(Node owner, Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Operation failed"); a.setHeaderText(null);
        a.setContentText(ex.getMessage() == null ? "Something went wrong." : ex.getMessage());
        if (owner != null && owner.getScene() != null) a.initOwner(owner.getScene().getWindow());
        a.showAndWait();
    }

    public static void showPatientDetails(Node owner, Patient patient) {
        if (patient == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Patient details");
        if (owner != null && owner.getScene() != null) dialog.initOwner(owner.getScene().getWindow());

        ButtonType close = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(close);

        VBox content = new VBox(14);
        content.setPadding(new Insets(8));
        content.setPrefWidth(520);

        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("patient-avatar");
        avatar.getChildren().add(icon(FontAwesomeSolid.USER, 28));

        VBox heading = new VBox(4);
        Label name = new Label(patient.getFullName());
        name.getStyleClass().add("dialog-title");
        Label code = new Label(patient.getPatientCode());
        code.getStyleClass().add("dialog-code");
        heading.getChildren().addAll(name, code);
        header.getChildren().addAll(avatar, heading);

        GridPane grid = new GridPane();
        grid.setHgap(18); grid.setVgap(11);
        addDetail(grid, 0, "Date of birth", String.valueOf(patient.getDateOfBirth()));
        addDetail(grid, 2, "Gender", String.valueOf(patient.getGender()));
        addDetail(grid, 0, "Contact", patient.getContactNumber());
        addDetail(grid, 2, "Email", patient.getEmail());
        addDetail(grid, 0, "Address", patient.getAddress());
        addDetail(grid, 2, "Registered", String.valueOf(patient.getRegisteredAt()));

        Label historyTitle = fieldLabel("Medical history");
        TextArea history = new TextArea(patient.getMedicalHistory() == null ? "" : patient.getMedicalHistory());
        history.setEditable(false); history.setWrapText(true); history.setPrefRowCount(5);
        history.getStyleClass().add("details-area");

        content.getChildren().addAll(header, new Separator(), grid, historyTitle, history);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getStylesheets().add(
                Ui.class.getResource("/lk/sunrise/dentalclinic/ui/app.css").toExternalForm()
        );
        dialog.showAndWait();
    }

    private static void addDetail(GridPane grid, int col, String label, String value) {
        Label l = fieldLabel(label);
        Label v = new Label(value == null || value.equals("null") ? "—" : value);
        v.setWrapText(true);
        v.getStyleClass().add("detail-value");
        grid.add(l, col, grid.getRowCount());
        grid.add(v, col + 1, grid.getRowCount() - 1);
    }
}

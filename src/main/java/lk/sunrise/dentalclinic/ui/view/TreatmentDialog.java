package lk.sunrise.dentalclinic.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lk.sunrise.dentalclinic.entity.Treatment;
import lk.sunrise.dentalclinic.ui.util.Ui;

public class TreatmentDialog {
    private final Dialog<Boolean> dialog = new Dialog<>();
    private final TextField name = Ui.textField("e.g. Dental Cleaning");
    private final TextField category = Ui.textField("e.g. Preventive");
    private final TextField price = Ui.textField("0.00");
    private final Spinner<Integer> duration = new Spinner<>();
    private final TextArea description = new TextArea();
    private final CheckBox active = new CheckBox("Active treatment");

    public TreatmentDialog(Treatment treatment) {
        boolean edit = treatment != null;

        dialog.setTitle(edit ? "Update Treatment" : "Add Treatment");
        dialog.setHeaderText(edit ? "Update treatment details" : "Add a new treatment");
        dialog.getDialogPane().getStyleClass().add("treatment-dialog");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/lk/sunrise/dentalclinic/ui/app.css").toExternalForm()
        );

        ButtonType saveType = new ButtonType(edit ? "Update treatment" : "Add treatment", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, cancelType);

        duration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1440, 30, 5));
        duration.setEditable(true);
        duration.getStyleClass().add("combo-box");

        description.setPromptText("Treatment description");
        description.setWrapText(true);
        description.setPrefRowCount(4);
        description.getStyleClass().add("text-area");

        active.setSelected(treatment == null || treatment.isActive());

        if (edit) {
            name.setText(treatment.getName());
            category.setText(treatment.getCategory() == null ? "" : treatment.getCategory());
            price.setText(treatment.getBasePrice() == null ? "" : treatment.getBasePrice().toPlainString());
            duration.getValueFactory().setValue(treatment.getDurationMinutes());
            description.setText(treatment.getDescription() == null ? "" : treatment.getDescription());
        }

        GridPane grid = Ui.grid();
        grid.setPadding(new Insets(4, 0, 0, 0));
        add(grid, 0, "Treatment name", name, 0);
        add(grid, 2, "Category", category, 0);
        add(grid, 0, "Base price", price, 1);
        add(grid, 2, "Duration (minutes)", duration, 1);
        grid.add(Ui.fieldLabel("Description"), 0, 2);
        grid.add(description, 1, 2, 3, 1);
        grid.add(active, 1, 3, 2, 1);

        VBox content = new VBox(14, grid);
        content.setPadding(new Insets(8));
        content.setPrefWidth(680);
        dialog.getDialogPane().setContent(content);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveType);
        saveButton.getStyleClass().add("primary-button");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelType);
        cancelButton.getStyleClass().add("outline-button");

        dialog.setResultConverter(button -> button == saveType);
    }

    private void add(GridPane grid, int column, String label, javafx.scene.Node node, int row) {
        grid.add(Ui.fieldLabel(label), column, row);
        grid.add(node, column + 1, row);
    }

    public boolean showAndWait() {
        return dialog.showAndWait().orElse(false);
    }

    public TextField name() {
        return name;
    }

    public TextField category() {
        return category;
    }

    public TextField price() {
        return price;
    }

    public Spinner<Integer> duration() {
        return duration;
    }

    public TextArea description() {
        return description;
    }

    public CheckBox active() {
        return active;
    }
}

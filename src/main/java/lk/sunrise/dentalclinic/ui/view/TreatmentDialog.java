package lk.sunrise.dentalclinic.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lk.sunrise.dentalclinic.entity.Treatment;
import lk.sunrise.dentalclinic.ui.util.Ui;
import lk.sunrise.dentalclinic.ui.util.Validation;

public class TreatmentDialog {
    private final Dialog<Boolean> dialog = new Dialog<>();
    private final TextField name = Ui.textField("e.g. Dental Cleaning");
    private final TextField category = Ui.textField("e.g. Preventive");
    private final TextField price = Ui.textField("0.00");
    private final Spinner<Integer> duration = new Spinner<>();
    private final TextArea description = new TextArea();
    private final CheckBox active = new CheckBox("Active treatment");
    private final Label nameError = validationLabel();
    private final Label priceError = validationLabel();
    private final Label durationError = validationLabel();

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
        add(grid, 0, "Treatment name", name, nameError, 0);
        add(grid, 2, "Category", category, 0);
        add(grid, 0, "Base price", price, priceError, 1);
        add(grid, 2, "Duration (minutes)", duration, durationError, 1);
        grid.add(Ui.fieldLabel("Description"), 0, 2);
        grid.add(description, 1, 2, 3, 1);
        grid.add(active, 1, 3, 2, 1);

        VBox content = new VBox(14, grid);
        content.setPadding(new Insets(8));
        content.setPrefWidth(680);
        dialog.getDialogPane().setContent(content);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveType);
        saveButton.getStyleClass().add("primary-button");
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateForm()) {
                event.consume();
            }
        });
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelType);
        cancelButton.getStyleClass().add("outline-button");

        bindRealtimeValidation();
        dialog.setResultConverter(button -> button == saveType);
    }

    private void add(GridPane grid, int column, String label, javafx.scene.Node node, int row) {
        grid.add(Ui.fieldLabel(label), column, row);
        grid.add(node, column + 1, row);
    }

    private void add(GridPane grid, int column, String label, javafx.scene.Node node, Label error, int row) {
        grid.add(Ui.fieldLabel(label), column, row);
        VBox fieldBox = new VBox(4, node, error);
        fieldBox.setFillWidth(true);
        grid.add(fieldBox, column + 1, row);
        GridPane.setHgrow(fieldBox, Priority.ALWAYS);
        if (node instanceof Region region) region.setMaxWidth(Double.MAX_VALUE);
    }

    private void bindRealtimeValidation() {
        name.textProperty().addListener((obs, old, value) -> markText(this::validateName, name, nameError));
        price.textProperty().addListener((obs, old, value) -> markText(this::validatePrice, price, priceError));
        duration.getEditor().textProperty().addListener((obs, old, value) -> markText(this::validateDuration, duration, durationError));
    }

    private boolean validateForm() {
        boolean valid = true;
        valid &= markText(this::validateName, name, nameError);
        valid &= markText(this::validatePrice, price, priceError);
        valid &= markText(this::validateDuration, duration, durationError);
        return valid;
    }

    private void validateName() {
        String value = name.getText();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Treatment name is required.");
        }
        if (!value.trim().matches("^[A-Za-z0-9][A-Za-z0-9 &()/'-]{2,149}$")) {
            throw new IllegalArgumentException("Treatment name contains invalid characters.");
        }
    }

    private void validatePrice() {
        String value = price.getText();
        if (value == null || !value.trim().matches("^\\d+(?:\\.\\d{1,2})?$")) {
            throw new IllegalArgumentException("Base price must be a valid amount, e.g. 2500 or 2500.50.");
        }
    }

    private void validateDuration() {
        String value = duration.getEditor().getText();
        if (value == null || !value.trim().matches("^\\d{1,4}$")) {
            throw new IllegalArgumentException("Duration must be a whole number of minutes.");
        }
        int minutes = Integer.parseInt(value.trim());
        if (minutes < 1 || minutes > 1440) {
            throw new IllegalArgumentException("Duration must be between 1 and 1440 minutes.");
        }
    }

    private boolean markText(Runnable validator, Control control, Label error) {
        try {
            validator.run();
            setValidationMessage(control, error, null);
            return true;
        } catch (IllegalArgumentException ex) {
            setValidationMessage(control, error, ex.getMessage());
            return false;
        }
    }

    private void setValidationMessage(Control control, Label error, String message) {
        boolean invalid = message != null && !message.isBlank();
        Validation.markInvalid(control, invalid);
        error.setText(invalid ? message : "");
        error.setVisible(invalid);
        error.setManaged(invalid);
    }

    private Label validationLabel() {
        Label label = new Label();
        label.getStyleClass().add("field-error-text");
        label.setWrapText(true);
        label.setVisible(false);
        label.setManaged(false);
        return label;
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

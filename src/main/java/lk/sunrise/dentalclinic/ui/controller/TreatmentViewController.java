package lk.sunrise.dentalclinic.ui.controller;

import lk.sunrise.dentalclinic.controller.TreatmentController;
import lk.sunrise.dentalclinic.entity.Treatment;
import lk.sunrise.dentalclinic.ui.util.Ui;
import lk.sunrise.dentalclinic.ui.view.TreatmentDialog;
import lk.sunrise.dentalclinic.ui.view.TreatmentView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class TreatmentViewController {
    private final TreatmentView view;
    private final TreatmentController controller = new TreatmentController();

    public TreatmentViewController(TreatmentView view) {
        this.view = view;
    }

    public void initialize() {
        view.add().setOnAction(e -> addTreatment());
        view.update().setOnAction(e -> editSelected());
        view.delete().setOnAction(e -> deleteSelected());
        view.refresh().setOnAction(e -> load());
        view.activeOnly().setOnAction(e -> load());
        view.search().textProperty().addListener((obs, oldValue, newValue) -> load());

        view.update().disableProperty().bind(view.table().getSelectionModel().selectedItemProperty().isNull());
        view.delete().disableProperty().bind(view.table().getSelectionModel().selectedItemProperty().isNull());

        load();
    }

    public void load() {
        try {
            List<Treatment> treatments = view.activeOnly().isSelected()
                    ? controller.getActive()
                    : controller.getAll();

            String keyword = view.search().getText() == null
                    ? ""
                    : view.search().getText().trim().toLowerCase(Locale.ROOT);

            if (!keyword.isBlank()) {
                treatments = treatments.stream()
                        .filter(t -> contains(t.getTreatmentCode(), keyword)
                                || contains(t.getName(), keyword)
                                || contains(t.getCategory(), keyword)
                                || contains(t.getDescription(), keyword))
                        .toList();
            }

            view.table().getItems().setAll(treatments);
        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    public void addTreatment() {
        TreatmentDialog dialog = new TreatmentDialog(null);

        if (!dialog.showAndWait()) {
            return;
        }

        try {
            controller.create(
                    dialog.name().getText(),
                    dialog.description().getText(),
                    dialog.category().getText(),
                    parsePrice(dialog.price().getText()),
                    parseDuration(dialog.duration().getEditor().getText()),
                    dialog.active().isSelected()
            );

            load();
            Ui.notify(view.root(), "Treatment added", "The treatment was saved successfully.", false);
        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    public void editSelected() {
        Treatment selected = view.table().getSelectionModel().getSelectedItem();

        if (selected == null) {
            Ui.error(view.root(), new IllegalArgumentException("Select a treatment first."));
            return;
        }

        TreatmentDialog dialog = new TreatmentDialog(selected);

        if (!dialog.showAndWait()) {
            return;
        }

        try {
            controller.update(
                    selected.getTreatmentId(),
                    dialog.name().getText(),
                    dialog.description().getText(),
                    dialog.category().getText(),
                    parsePrice(dialog.price().getText()),
                    parseDuration(dialog.duration().getEditor().getText()),
                    dialog.active().isSelected()
            );

            load();
            Ui.notify(view.root(), "Treatment updated", "The treatment changes were saved.", false);
        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    public void deleteSelected() {
        Treatment selected = view.table().getSelectionModel().getSelectedItem();

        if (selected == null) {
            Ui.error(view.root(), new IllegalArgumentException("Select a treatment first."));
            return;
        }

        javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION
        );
        confirmation.setTitle("Delete treatment");
        confirmation.setHeaderText("Delete " + selected.getName() + "?");
        confirmation.setContentText(
                "This permanently removes the treatment from the catalogue. " +
                        "If it has already been used by appointments or treatment records, " +
                        "the database will prevent the deletion."
        );
        if (view.root().getScene() != null) {
            confirmation.initOwner(view.root().getScene().getWindow());
        }

        if (confirmation.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL)
                != javafx.scene.control.ButtonType.OK) {
            return;
        }

        try {
            controller.delete(selected.getTreatmentId());
            load();
            Ui.notify(view.root(), "Treatment deleted", "The treatment was removed.", false);
        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    private BigDecimal parsePrice(String value) {
        if (value == null || !value.trim().matches("^\\d+(?:\\.\\d{1,2})?$")) {
            throw new IllegalArgumentException("Base price must be a valid amount, e.g. 2500 or 2500.50.");
        }
        return new BigDecimal(value.trim());
    }

    private int parseDuration(String value) {
        if (value == null || !value.trim().matches("^\\d{1,4}$")) {
            throw new IllegalArgumentException("Duration must be a whole number of minutes.");
        }
        return Integer.parseInt(value.trim());
    }
}

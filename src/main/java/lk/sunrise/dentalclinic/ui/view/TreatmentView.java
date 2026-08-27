package lk.sunrise.dentalclinic.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lk.sunrise.dentalclinic.entity.Treatment;
import lk.sunrise.dentalclinic.ui.controller.TreatmentViewController;
import lk.sunrise.dentalclinic.ui.util.Ui;

public class TreatmentView {
    private final VBox root = new VBox(18);
    private final TextField search = Ui.textField("Search treatment / code / category");
    private final TableView<Treatment> table = new TableView<>();
    private final CheckBox activeOnly = new CheckBox("Active only");
    private final Button add = Ui.button("Add treatment", "primary-button");
    private final Button update = Ui.button("Update selected", "secondary-button");
    private final Button delete = Ui.button("Delete selected", "danger-button");
    private final Button refresh = Ui.button("Refresh", "outline-button");
    private final TreatmentViewController controller;

    public TreatmentView() {
        controller = new TreatmentViewController(this);

        root.setPadding(new Insets(24));

        Label title = new Label("Treatment management");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label(
                "Maintain the treatment catalogue, pricing and availability used by appointments and billing."
        );
        subtitle.getStyleClass().add("page-subtitle");

        HBox actions = Ui.row(add, update, delete, refresh);
        actions.setPadding(new Insets(2, 0, 4, 0));

        HBox searchBar = Ui.row(search, activeOnly);
        HBox.setHgrow(search, javafx.scene.layout.Priority.ALWAYS);

        configureTable();

        root.getChildren().addAll(title, subtitle, actions, searchBar, table);
        Ui.grow(table);

        controller.initialize();
    }

    private void configureTable() {
        table.getColumns().addAll(
                col("Code", "treatmentCode", 130),
                col("Treatment", "name", 210),
                col("Category", "category", 150),
                priceColumn(),
                col("Duration", "durationMinutes", 110),
                activeColumn()
        );

        table.setPlaceholder(new Label("No treatments found."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                controller.editSelected();
            }
        });
    }

    private <T> TableColumn<Treatment, T> col(String title, String property, double width) {
        TableColumn<Treatment, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    private TableColumn<Treatment, java.math.BigDecimal> priceColumn() {
        TableColumn<Treatment, java.math.BigDecimal> column = new TableColumn<>("Base price");
        column.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        column.setPrefWidth(130);
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(java.math.BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? "—" : "LKR " + value.toPlainString());
            }
        });
        return column;
    }

    private TableColumn<Treatment, Boolean> activeColumn() {
        TableColumn<Treatment, Boolean> column = new TableColumn<>("Status");
        column.setCellValueFactory(new PropertyValueFactory<>("active"));
        column.setPrefWidth(110);
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                setText(empty || active == null ? "" : active ? "ACTIVE" : "INACTIVE");
                getStyleClass().removeAll("status-active", "status-inactive");
                if (!empty && active != null) {
                    getStyleClass().add(active ? "status-active" : "status-inactive");
                }
            }
        });
        return column;
    }

    public VBox root() {
        return root;
    }

    public TextField search() {
        return search;
    }

    public TableView<Treatment> table() {
        return table;
    }

    public CheckBox activeOnly() {
        return activeOnly;
    }

    public Button add() {
        return add;
    }

    public Button update() {
        return update;
    }

    public Button delete() {
        return delete;
    }

    public Button refresh() {
        return refresh;
    }
}

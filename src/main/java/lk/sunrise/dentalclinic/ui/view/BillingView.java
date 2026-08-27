package lk.sunrise.dentalclinic.ui.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import lk.sunrise.dentalclinic.entity.Appointment;
import lk.sunrise.dentalclinic.entity.PaymentMethod;
import lk.sunrise.dentalclinic.ui.controller.BillingViewController;
import lk.sunrise.dentalclinic.ui.util.Ui;
import org.controlsfx.control.SearchableComboBox;

public class BillingView {

    private final VBox root = new VBox(18);

    private final SearchableComboBox<Appointment> appointment = new SearchableComboBox<>();

    private final Button viewPatient =
            Ui.button("View patient", "outline-button");

    private final Label patientSummary =
            new Label("Select an appointment to verify the patient.");

    private final TextField tax =
            Ui.textField("0");

    private final TextField discount =
            Ui.textField("0");

    private final Button generate =
            Ui.button("Generate invoice", "primary-button");

    private final Button downloadPdf =
            Ui.button("Download PDF", "outline-button");

    private final TextField invoiceId =
            Ui.textField("Invoice ID");

    private final Button loadInvoice =
            Ui.button("Load invoice", "secondary-button");

    private final TextField amount =
            Ui.textField("Amount paid");

    private final ComboBox<PaymentMethod> method =
            new ComboBox<>();

    private final Button pay =
            Ui.button("Record payment", "secondary-button");

    private final TextArea result =
            new TextArea();

    private final BillingViewController controller;

    public BillingView() {

        controller = new BillingViewController(this);

        root.setPadding(new Insets(24));

        Label title = new Label("Billing & payments");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label(
                "Load an appointment, verify the patient, generate the invoice and record real payments."
        );
        subtitle.getStyleClass().add("page-subtitle");

        /*
         * ============================================================
         * APPOINTMENT / INVOICE FORM
         * ============================================================
         */

        GridPane invoiceGrid = Ui.grid();

        invoiceGrid.setHgap(18);
        invoiceGrid.setVgap(18);

        invoiceGrid.add(
                Ui.fieldLabel("Appointment"),
                0,
                0
        );

        invoiceGrid.add(
                appointment,
                1,
                0,
                2,
                1
        );

        invoiceGrid.add(
                viewPatient,
                3,
                0
        );

        invoiceGrid.add(
                Ui.fieldLabel("Patient"),
                0,
                1
        );

        invoiceGrid.add(
                patientSummary,
                1,
                1,
                3,
                1
        );

        patientSummary.getStyleClass().add("selection-summary");

        invoiceGrid.add(
                Ui.fieldLabel("Tax %"),
                0,
                2
        );

        invoiceGrid.add(
                tax,
                1,
                2
        );

        invoiceGrid.add(
                Ui.fieldLabel("Discount"),
                2,
                2
        );

        invoiceGrid.add(
                discount,
                3,
                2
        );

        HBox invoiceActions = Ui.row(
                generate,
                downloadPdf
        );

        invoiceGrid.add(
                invoiceActions,
                2,
                3,
                2,
                1
        );

        VBox invoiceCard = Ui.card("Generate invoice");

        invoiceCard.getChildren().add(invoiceGrid);

        /*
         * ============================================================
         * PAYMENT FORM
         * ============================================================
         */

        GridPane paymentGrid = Ui.grid();

        paymentGrid.setHgap(18);
        paymentGrid.setVgap(18);

        paymentGrid.add(
                Ui.fieldLabel("Invoice ID"),
                0,
                0
        );

        paymentGrid.add(
                invoiceId,
                1,
                0
        );

        paymentGrid.add(
                loadInvoice,
                2,
                0
        );

        paymentGrid.add(
                Ui.fieldLabel("Amount"),
                0,
                1
        );

        paymentGrid.add(
                amount,
                1,
                1
        );

        paymentGrid.add(
                Ui.fieldLabel("Method"),
                2,
                1
        );

        paymentGrid.add(
                method,
                3,
                1
        );

        paymentGrid.add(
                pay,
                3,
                2
        );

        VBox paymentCard = Ui.card("Payment");

        paymentCard.getChildren().add(paymentGrid);

        /*
         * ============================================================
         * APPOINTMENT SEARCHABLE COMBOBOX
         * ============================================================
         */

        appointment.setPromptText(
                "Search appointment / patient code / patient name..."
        );

        appointment.setMaxWidth(Double.MAX_VALUE);

        appointment.setConverter(new StringConverter<>() {

            @Override
            public String toString(Appointment a) {

                if (a == null) {
                    return "";
                }

                String patientText = "";

                if (a.getPatient() != null) {

                    patientText =
                            a.getPatient().getPatientCode()
                                    + " "
                                    + a.getPatient().getFullName();
                }

                return a.getAppointmentNo()
                        + " — "
                        + patientText
                        + " — "
                        + a.getAppointmentDate()
                        + " "
                        + a.getStatus();
            }

            @Override
            public Appointment fromString(String value) {
                return appointment.getValue();
            }
        });

        /*
         * Dropdown display
         */

        appointment.setCellFactory(list -> new ListCell<>() {

            @Override
            protected void updateItem(
                    Appointment item,
                    boolean empty
            ) {

                super.updateItem(item, empty);

                if (empty || item == null) {

                    setText(null);

                    return;
                }

                String patientText = "";

                if (item.getPatient() != null) {

                    patientText =
                            item.getPatient().getPatientCode()
                                    + " — "
                                    + item.getPatient().getFullName();
                }

                setText(
                        item.getAppointmentNo()
                                + " — "
                                + patientText
                                + " — "
                                + item.getAppointmentDate()
                                + " "
                                + item.getStatus()
                );
            }
        });

        /*
         * Selected appointment display
         */

        appointment.setButtonCell(new ListCell<>() {

            @Override
            protected void updateItem(
                    Appointment item,
                    boolean empty
            ) {

                super.updateItem(item, empty);

                if (empty || item == null) {

                    setText(null);

                    return;
                }

                String patientText = "";

                if (item.getPatient() != null) {

                    patientText =
                            item.getPatient().getPatientCode()
                                    + " — "
                                    + item.getPatient().getFullName();
                }

                setText(
                        item.getAppointmentNo()
                                + " — "
                                + patientText
                                + " — "
                                + item.getAppointmentDate()
                                + " "
                                + item.getStatus()
                );
            }
        });

        /*
         * ============================================================
         * PAYMENT METHOD
         * ============================================================
         */

        method.setItems(
                FXCollections.observableArrayList(
                        PaymentMethod.values()
                )
        );

        method.setPromptText("Payment method");

        /*
         * ============================================================
         * RESULT
         * ============================================================
         */

        result.setEditable(false);

        result.setWrapText(true);

        result.setPrefRowCount(11);

        result.getStyleClass().add("invoice-result");

        Ui.grow(result);

        VBox.setVgrow(result, Priority.ALWAYS);

        /*
         * ============================================================
         * ROOT
         * ============================================================
         */

        root.getChildren().addAll(
                title,
                subtitle,
                invoiceCard,
                paymentCard,
                result
        );

        controller.initialize();
    }

    public VBox root() {
        return root;
    }

    public SearchableComboBox<Appointment> appointment() {
        return appointment;
    }

    public Button viewPatient() {
        return viewPatient;
    }

    public Label patientSummary() {
        return patientSummary;
    }

    public TextField tax() {
        return tax;
    }

    public TextField discount() {
        return discount;
    }

    public Button generate() {
        return generate;
    }

    public Button downloadPdf() {
        return downloadPdf;
    }

    public TextField invoiceId() {
        return invoiceId;
    }

    public Button loadInvoice() {
        return loadInvoice;
    }

    public TextField amount() {
        return amount;
    }

    public ComboBox<PaymentMethod> method() {
        return method;
    }

    public Button pay() {
        return pay;
    }

    public TextArea result() {
        return result;
    }
}
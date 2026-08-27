package lk.sunrise.dentalclinic.ui.controller;

import javafx.collections.FXCollections;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import lk.sunrise.dentalclinic.controller.AppointmentController;
import lk.sunrise.dentalclinic.controller.BillingController;
import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.ui.util.Ui;
import lk.sunrise.dentalclinic.ui.util.Validation;
import lk.sunrise.dentalclinic.ui.view.BillingView;
import lk.sunrise.dentalclinic.util.InvoicePdfGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillingViewController {

    private final BillingView view;
    private final BillingController controller = new BillingController();
    private final AppointmentController appointments = new AppointmentController();

    private List<Appointment> completedAppointments = new ArrayList<>();

    private InvoiceDTO currentInvoice;
    private Appointment currentAppointment;

    public BillingViewController(BillingView view) {
        this.view = view;
    }

    public void initialize() {
        try {
            loadCompletedAppointments();

            TextField editor = view.appointment().getEditor();

            editor.textProperty().addListener(
                    (obs, old, value) -> filterAppointments(value)
            );

            view.appointment()
                    .getSelectionModel()
                    .selectedItemProperty()
                    .addListener(
                            (obs, old, a) -> updatePatient(a)
                    );

            view.generate().setOnAction(e -> generate());
            view.pay().setOnAction(e -> pay());
            view.loadInvoice().setOnAction(e -> loadInvoice());
            view.viewPatient().setOnAction(e -> viewPatient());
            view.downloadPdf().setOnAction(e -> downloadPdf());

            view.downloadPdf().setDisable(true);

        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    private void loadCompletedAppointments() throws Exception {
        LocalDate from = LocalDate.now().minusYears(2);
        LocalDate to = LocalDate.now().plusYears(1);

        completedAppointments = appointments
                .daily(from, to, null)
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .toList();

        view.appointment().setItems(
                FXCollections.observableArrayList(completedAppointments)
        );
    }

    private void filterAppointments(String text) {
        if (text == null) return;

        String q = text.trim().toLowerCase();

        List<Appointment> filtered = completedAppointments
                .stream()
                .filter(a -> {
                    String patient = a.getPatient() == null
                            ? ""
                            : a.getPatient().getPatientCode()
                            + " "
                            + a.getPatient().getFullName();

                    String treatment = a.getTreatment() == null
                            ? ""
                            : a.getTreatment().getName();

                    String haystack =
                            a.getAppointmentNo()
                                    + " "
                                    + patient
                                    + " "
                                    + treatment
                                    + " "
                                    + a.getAppointmentDate();

                    return haystack.toLowerCase().contains(q);
                })
                .toList();

        view.appointment().setItems(
                FXCollections.observableArrayList(filtered)
        );

        if (!view.appointment().getEditor().getText().equals(text)) {
            view.appointment().getEditor().setText(text);
        }

        if (!filtered.isEmpty()) {
            view.appointment().show();
        }
    }

    private void updatePatient(Appointment a) {
        currentAppointment = a;

        if (a == null) {
            view.patientSummary().setText(
                    "Select a completed appointment to verify the patient."
            );
            return;
        }

        Patient p = a.getPatient();

        view.patientSummary().setText(
                p == null
                        ? "Patient unavailable"
                        : p.getPatientCode()
                        + "  •  "
                        + p.getFullName()
                        + "  •  "
                        + p.getContactNumber()
                        + "  •  Appointment: "
                        + a.getStatus()
        );
    }

    private void generate() {
        try {
            Appointment a = view.appointment().getValue();

            if (a == null) {
                throw new IllegalArgumentException(
                        "Select a completed appointment first."
                );
            }

            if (a.getStatus() != AppointmentStatus.COMPLETED) {
                throw new IllegalArgumentException(
                        "Only completed appointments can be invoiced."
                );
            }

            BigDecimal tax = Validation.decimal(
                    view.tax().getText(),
                    "Tax"
            );

            BigDecimal discount = Validation.decimal(
                    view.discount().getText(),
                    "Discount"
            );

            if (tax.signum() < 0) {
                throw new IllegalArgumentException(
                        "Tax cannot be negative."
                );
            }

            if (discount.signum() < 0) {
                throw new IllegalArgumentException(
                        "Discount cannot be negative."
                );
            }

            InvoiceDTO invoice = controller.generateInvoice(
                    a.getAppointmentId(),
                    tax,
                    discount
            );

            currentInvoice = invoice;
            currentAppointment = a;

            view.invoiceId().setText(
                    String.valueOf(invoice.getInvoiceId())
            );

            view.result().setText(
                    format(invoice)
            );

            view.downloadPdf().setDisable(false);

            Ui.notify(
                    view.root(),
                    "Invoice generated",
                    invoice.getInvoiceNo() + " created successfully.",
                    false
            );

        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    private void loadInvoice() {
        try {
            String value = view.invoiceId().getText().trim();

            if (value.isBlank()) {
                throw new IllegalArgumentException(
                        "Enter an invoice ID."
                );
            }

            int id = Integer.parseInt(value);

            InvoiceDTO invoice = controller.getInvoice(id);

            currentInvoice = invoice;

            currentAppointment =
                    appointments.getById(invoice.getAppointmentId());

            view.result().setText(
                    format(invoice)
            );

            view.downloadPdf().setDisable(false);

            Ui.notify(
                    view.root(),
                    "Invoice loaded",
                    invoice.getInvoiceNo() + " loaded.",
                    false
            );

        } catch (NumberFormatException e) {
            Ui.error(
                    view.root(),
                    new IllegalArgumentException(
                            "Invoice ID must be a valid number."
                    )
            );
        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    private void downloadPdf() {
        try {
            if (currentInvoice == null) {
                throw new IllegalArgumentException(
                        "Generate or load an invoice first."
                );
            }

            if (currentAppointment == null) {
                currentAppointment =
                        appointments.getById(
                                currentInvoice.getAppointmentId()
                        );
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save invoice PDF");
            chooser.setInitialFileName(
                    currentInvoice.getInvoiceNo() + ".pdf"
            );
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "PDF files",
                            "*.pdf"
                    )
            );

            File file = chooser.showSaveDialog(
                    view.root().getScene().getWindow()
            );

            if (file == null) {
                return;
            }

            try (FileOutputStream output =
                         new FileOutputStream(file)) {

                InvoicePdfGenerator.generate(
                        currentInvoice,
                        currentAppointment,
                        output
                );
            }

            Ui.notify(
                    view.root(),
                    "PDF saved",
                    "Invoice PDF was saved successfully.",
                    false
            );

        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    private void pay() {
        try {
            String invoiceText =
                    view.invoiceId().getText().trim();

            if (invoiceText.isBlank()) {
                throw new IllegalArgumentException(
                        "Enter an invoice ID or generate an invoice first."
                );
            }

            int id = Integer.parseInt(invoiceText);

            if (view.method().getValue() == null) {
                throw new IllegalArgumentException(
                        "Select a payment method."
                );
            }

            BigDecimal amount = Validation.decimal(
                    view.amount().getText(),
                    "Amount paid"
            );

            if (amount.signum() <= 0) {
                throw new IllegalArgumentException(
                        "Amount paid must be greater than zero."
                );
            }

            PaymentDTO payment = controller.recordPayment(
                    new PaymentDTO(
                            0,
                            id,
                            amount,
                            null,
                            view.method().getValue()
                    )
            );

            InvoiceDTO updated =
                    controller.getInvoice(id);

            currentInvoice = updated;

            view.result().setText(
                    format(updated)
                            + "\n\nPayment recorded: "
                            + payment.getAmountPaid()
                            + " via "
                            + payment.getMethod()
                            + " at "
                            + payment.getPaymentDate()
            );

            view.downloadPdf().setDisable(false);

            Ui.notify(
                    view.root(),
                    "Payment saved",
                    "Payment recorded successfully.",
                    false
            );

        } catch (NumberFormatException e) {
            Ui.error(
                    view.root(),
                    new IllegalArgumentException(
                            "Invoice ID must be a valid number."
                    )
            );
        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    private void viewPatient() {
        Appointment a = view.appointment().getValue();

        if (a == null || a.getPatient() == null) {
            Ui.error(
                    view.root(),
                    new IllegalArgumentException(
                            "Select an appointment first."
                    )
            );
            return;
        }

        Ui.showPatientDetails(
                view.root(),
                a.getPatient()
        );
    }

    private String format(InvoiceDTO i) {
        StringBuilder text = new StringBuilder();

        text.append("Invoice: ")
                .append(i.getInvoiceNo())
                .append("\n");

        text.append("Appointment ID: ")
                .append(i.getAppointmentId())
                .append("\n");

        text.append("Patient ID: ")
                .append(i.getPatientId())
                .append("\n\n");

        text.append("Subtotal: ")
                .append(i.getSubTotal())
                .append("\n");

        text.append("Consultation: ")
                .append(i.getConsultationFee())
                .append("\n");

        text.append("Tax (")
                .append(i.getTaxRate())
                .append("%): ")
                .append(i.getTaxAmount())
                .append("\n");

        text.append("Discount: ")
                .append(i.getDiscount())
                .append("\n");

        text.append("Total: ")
                .append(i.getTotalAmount())
                .append("\n");

        text.append("Status: ")
                .append(i.getStatus())
                .append("\n\n");

        text.append("Items:\n");

        if (i.getItems() == null || i.getItems().isEmpty()) {
            text.append("No invoice items.");
        } else {
            for (InvoiceItemDTO item : i.getItems()) {
                text.append("• ")
                        .append(item.getDescription())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(" = ")
                        .append(item.getLineTotal())
                        .append("\n");
            }
        }

        return text.toString();
    }
}

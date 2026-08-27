package lk.sunrise.dentalclinic.ui.controller;

import javafx.collections.FXCollections;
import lk.sunrise.dentalclinic.controller.ReportController;
import lk.sunrise.dentalclinic.dto.ReportRequestDTO;
import lk.sunrise.dentalclinic.dto.RevenueReportDTO;
import lk.sunrise.dentalclinic.entity.Appointment;
import lk.sunrise.dentalclinic.report.ReportPdfGenerator;
import lk.sunrise.dentalclinic.ui.util.Ui;
import lk.sunrise.dentalclinic.ui.view.ReportsView;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

public class ReportsViewController {

    private final ReportsView view;
    private final ReportController controller = new ReportController();
    private final ReportPdfGenerator pdfGenerator = new ReportPdfGenerator();

    public ReportsViewController(ReportsView v) {
        view = v;
    }

    public void initialize() {

        view.format().setItems(
                FXCollections.observableArrayList(
                        "SCREEN",
                        "PDF",
                        "EXCEL",
                        "JSON"
                )
        );

        view.format().setValue("SCREEN");

        view.daily().setOnAction(e -> daily());
        view.monthly().setOnAction(e -> monthly());
    }

    private void daily() {

        try {

            if (view.from().getValue() == null ||
                    view.to().getValue() == null) {

                throw new IllegalArgumentException(
                        "Please select both From and To dates."
                );
            }

            if (view.from().getValue().isAfter(view.to().getValue())) {

                throw new IllegalArgumentException(
                        "From date cannot be after To date."
                );
            }

            String output = view.format().getValue();

            if (output == null) {
                output = "SCREEN";
            }

            ReportRequestDTO request =
                    new ReportRequestDTO(
                            view.from().getValue(),
                            view.to().getValue(),
                            null,
                            output
                    );

            List<Appointment> appointments =
                    controller.dailyAppointments(request);

            if ("PDF".equals(output)) {

                File file =
                        pdfGenerator.generateDailyAppointmentReport(
                                appointments,
                                view.from().getValue(),
                                view.to().getValue()
                        );

                view.summary().setText(
                        "PDF generated: " + file.getAbsolutePath()
                );

                openFile(file);

                return;
            }

            if ("SCREEN".equals(output)) {

                view.table()
                        .getItems()
                        .setAll(appointments);

                view.summary().setText(
                        "Appointments returned: " +
                                appointments.size()
                );

                return;
            }

            if ("EXCEL".equals(output)) {

                view.summary().setText(
                        "Excel export is not implemented yet."
                );

                view.table()
                        .getItems()
                        .setAll(appointments);

                return;
            }

            if ("JSON".equals(output)) {

                view.summary().setText(
                        "JSON export is not implemented yet."
                );

                view.table()
                        .getItems()
                        .setAll(appointments);

                return;
            }

        } catch (Exception e) {

            Ui.error(view.root(), e);
        }
    }

    private void monthly() {

        try {

            Integer selectedYear = view.year().getValue();
            Integer selectedMonth = view.month().getValue();

            if (selectedYear == null ||
                    selectedMonth == null) {

                throw new IllegalArgumentException(
                        "Please select a valid year and month."
                );
            }

            RevenueReportDTO report =
                    controller.monthlyRevenue(
                            selectedYear,
                            selectedMonth
                    );

            view.summary().setText(
                    "Invoices: " +
                            report.getInvoiceCount() +
                            "   Revenue: " +
                            report.getRevenue() +
                            "   Tax: " +
                            report.getTax() +
                            "   Collected: " +
                            report.getCollected() +
                            "   Outstanding: " +
                            report.getOutstanding()
            );

            File file =
                    pdfGenerator.generateMonthlyRevenueReport(
                            report,
                            selectedYear,
                            selectedMonth
                    );

            openFile(file);

        } catch (Exception e) {

            Ui.error(view.root(), e);
        }
    }

    private void openFile(File file) throws Exception {

        if (!Desktop.isDesktopSupported()) {

            view.summary().setText(
                    "PDF generated: " +
                            file.getAbsolutePath()
            );

            return;
        }

        Desktop desktop = Desktop.getDesktop();

        if (!desktop.isSupported(Desktop.Action.OPEN)) {

            view.summary().setText(
                    "PDF generated: " +
                            file.getAbsolutePath()
            );

            return;
        }

        desktop.open(file);
    }
}
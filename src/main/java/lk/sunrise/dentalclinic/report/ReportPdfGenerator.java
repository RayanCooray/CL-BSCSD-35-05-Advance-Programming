package lk.sunrise.dentalclinic.report;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lk.sunrise.dentalclinic.dto.RevenueReportDTO;
import lk.sunrise.dentalclinic.entity.Appointment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

public class ReportPdfGenerator {

    private final File reportDirectory;

    public ReportPdfGenerator() {
        reportDirectory = new File("reports");

        if (!reportDirectory.exists() && !reportDirectory.mkdirs()) {
            throw new IllegalStateException("Could not create reports directory.");
        }
    }

    public File generateDailyAppointmentReport(
            List<Appointment> appointments,
            LocalDate from,
            LocalDate to
    ) throws Exception {

        File file = new File(
                reportDirectory,
                "daily-appointment-report-" + from + "-" + to + ".pdf"
        );

        StringBuilder rows = new StringBuilder();

        for (Appointment appointment : appointments) {

            String patientCode = "-";
            String patientName = "-";
            String dentistName = "-";
            String treatmentName = "-";

            if (appointment.getPatient() != null) {
                patientCode = safe(
                        appointment.getPatient().getPatientCode()
                );

                patientName = safe(
                        appointment.getPatient().getFullName()
                );
            }

            if (appointment.getDentist() != null) {
                dentistName = safe(
                        appointment.getDentist().getFullName()
                );
            }

            if (appointment.getTreatment() != null) {
                treatmentName = safe(
                        appointment.getTreatment().getName()
                );
            }

            rows.append("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                    </tr>
                    """.formatted(
                    safe(appointment.getAppointmentNo()),
                    patientCode,
                    patientName,
                    dentistName,
                    treatmentName,
                    appointment.getAppointmentDate(),
                    appointment.getStartTime(),
                    appointment.getStatus()
            ));
        }

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8"/>
                
                    <style>
                        @page {
                            size: A4 landscape;
                            margin: 35px;
                        }
                
                        body {
                            font-family: Arial, sans-serif;
                            color: #1f2937;
                            font-size: 10px;
                        }
                
                        .header {
                            text-align: center;
                            margin-bottom: 25px;
                        }
                
                        .clinic {
                            font-size: 24px;
                            font-weight: bold;
                        }
                
                        .title {
                            font-size: 17px;
                            font-weight: bold;
                            margin-top: 8px;
                        }
                
                        .period {
                            color: #64748b;
                            margin-top: 6px;
                        }
                
                        table {
                            width: 100%%;
                            border-collapse: collapse;
                            margin-top: 20px;
                        }
                
                        th {
                            background: #1f2937;
                            color: white;
                            padding: 8px;
                            text-align: left;
                        }
                
                        td {
                            border: 1px solid #d1d5db;
                            padding: 7px;
                        }
                
                        tr:nth-child(even) {
                            background: #f8fafc;
                        }
                
                        .summary {
                            margin-top: 18px;
                            font-size: 12px;
                            font-weight: bold;
                        }
                
                        .footer {
                            margin-top: 30px;
                            text-align: center;
                            color: #64748b;
                            font-size: 9px;
                        }
                    </style>
                </head>
                
                <body>
                
                    <div class="header">
                        <div class="clinic">
                            SUNRISE DENTAL
                        </div>
                
                        <div class="title">
                            Daily Appointment Report
                        </div>
                
                        <div class="period">
                            %s to %s
                        </div>
                    </div>
                
                    <table>
                        <thead>
                            <tr>
                                <th>Appointment No</th>
                                <th>Patient Code</th>
                                <th>Patient</th>
                                <th>Dentist</th>
                                <th>Treatment</th>
                                <th>Date</th>
                                <th>Start</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                
                        <tbody>
                            %s
                        </tbody>
                    </table>
                
                    <div class="summary">
                        Total appointments: %d
                    </div>
                
                    <div class="footer">
                        Sunrise Dental Clinic Management System
                    </div>
                
                </body>
                </html>
                """.formatted(
                from,
                to,
                rows,
                appointments.size()
        );

        render(html, file);

        return file;
    }

    public File generateMonthlyRevenueReport(
            RevenueReportDTO report,
            int year,
            int month
    ) throws Exception {

        File file = new File(
                reportDirectory,
                "monthly-revenue-report-"
                        + year
                        + "-"
                        + String.format("%02d", month)
                        + ".pdf"
        );

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8"/>
                
                    <style>
                        @page {
                            size: A4;
                            margin: 45px;
                        }
                
                        body {
                            font-family: Arial, sans-serif;
                            color: #1f2937;
                            font-size: 12px;
                        }
                
                        .header {
                            text-align: center;
                            margin-bottom: 35px;
                        }
                
                        .clinic {
                            font-size: 25px;
                            font-weight: bold;
                        }
                
                        .title {
                            font-size: 19px;
                            font-weight: bold;
                            margin-top: 10px;
                        }
                
                        .period {
                            color: #64748b;
                            margin-top: 6px;
                        }
                
                        table {
                            width: 100%%;
                            border-collapse: collapse;
                            margin-top: 30px;
                        }
                
                        th {
                            background: #1f2937;
                            color: white;
                            padding: 12px;
                            text-align: left;
                        }
                
                        td {
                            border: 1px solid #d1d5db;
                            padding: 12px;
                        }
                
                        .amount {
                            text-align: right;
                        }
                
                        .outstanding {
                            margin-top: 25px;
                            padding: 15px;
                            background: #f1f5f9;
                            font-size: 15px;
                            font-weight: bold;
                        }
                
                        .footer {
                            margin-top: 45px;
                            text-align: center;
                            color: #64748b;
                            font-size: 9px;
                        }
                    </style>
                </head>
                
                <body>
                
                    <div class="header">
                        <div class="clinic">
                            SUNRISE DENTAL
                        </div>
                
                        <div class="title">
                            Monthly Revenue Report
                        </div>
                
                        <div class="period">
                            %d / %02d
                        </div>
                    </div>
                
                    <table>
                        <tr>
                            <th>Invoice Count</th>
                            <td>%d</td>
                        </tr>
                
                        <tr>
                            <th>Total Revenue</th>
                            <td class="amount">
                                LKR %s
                            </td>
                        </tr>
                
                        <tr>
                            <th>Total Tax</th>
                            <td class="amount">
                                LKR %s
                            </td>
                        </tr>
                
                        <tr>
                            <th>Total Collected</th>
                            <td class="amount">
                                LKR %s
                            </td>
                        </tr>
                
                        <tr>
                            <th>Outstanding</th>
                            <td class="amount">
                                LKR %s
                            </td>
                        </tr>
                    </table>
                
                    <div class="outstanding">
                        Outstanding Balance: LKR %s
                    </div>
                
                    <div class="footer">
                        Sunrise Dental Clinic Management System
                    </div>
                
                </body>
                </html>
                """.formatted(
                year,
                month,
                report.getInvoiceCount(),
                report.getRevenue(),
                report.getTax(),
                report.getCollected(),
                report.getOutstanding(),
                report.getOutstanding()
        );

        render(html, file);

        return file;
    }

    private void render(
            String html,
            File file
    ) throws Exception {

        try (OutputStream output =
                     new FileOutputStream(file)) {

            new PdfRendererBuilder()
                    .useFastMode()
                    .withHtmlContent(html, null)
                    .toStream(output)
                    .run();
        }
    }

    private String safe(Object value) {

        if (value == null) {
            return "";
        }

        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
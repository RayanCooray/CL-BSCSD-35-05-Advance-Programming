package lk.sunrise.dentalclinic.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lk.sunrise.dentalclinic.dto.InvoiceDTO;
import lk.sunrise.dentalclinic.dto.InvoiceItemDTO;
import lk.sunrise.dentalclinic.entity.Appointment;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public final class InvoicePdfGenerator {

    private InvoicePdfGenerator() {}

    public static void generate(
            InvoiceDTO invoice,
            Appointment appointment,
            OutputStream output
    ) throws Exception {

        if (invoice == null) {
            throw new IllegalArgumentException("Invoice is required.");
        }

        if (appointment == null) {
            throw new IllegalArgumentException("Appointment details are required.");
        }

        String patientName = appointment.getPatient() == null
                ? "—"
                : safe(appointment.getPatient().getFullName());

        String patientCode = appointment.getPatient() == null
                ? "—"
                : safe(appointment.getPatient().getPatientCode());

        String patientContact = appointment.getPatient() == null
                ? "—"
                : safe(appointment.getPatient().getContactNumber());

        String dentistName = appointment.getDentist() == null
                ? "—"
                : safe(appointment.getDentist().getFullName());

        String treatmentName = appointment.getTreatment() == null
                ? "Dental treatment"
                : safe(appointment.getTreatment().getName());

        StringBuilder items = new StringBuilder();

        if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
            for (InvoiceItemDTO item : invoice.getItems()) {
                items.append("""
                        <tr>
                            <td>%s</td>
                            <td class="center">%d</td>
                            <td class="right">%s</td>
                            <td class="right">%s</td>
                        </tr>
                        """.formatted(
                        safe(item.getDescription()),
                        item.getQuantity(),
                        money(item.getUnitPrice()),
                        money(item.getLineTotal())
                ));
            }
        } else {
            items.append("""
                    <tr>
                        <td>%s</td>
                        <td class="center">1</td>
                        <td class="right">%s</td>
                        <td class="right">%s</td>
                    </tr>
                    """.formatted(
                    treatmentName,
                    money(invoice.getSubTotal()),
                    money(invoice.getSubTotal())
            ));
        }

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8"/>
                    <style>
                        @page {
                            size: A4;
                            margin: 42px;
                        }

                        body {
                            font-family: Arial, sans-serif;
                            color: #1f2937;
                            font-size: 12px;
                        }

                        .header {
                            border-bottom: 2px solid #26324f;
                            padding-bottom: 14px;
                            margin-bottom: 22px;
                        }

                        .clinic {
                            font-size: 24px;
                            font-weight: bold;
                            color: #26324f;
                        }

                        .subtitle {
                            color: #64748b;
                            margin-top: 4px;
                        }

                        .invoice-title {
                            font-size: 22px;
                            font-weight: bold;
                            margin-top: 22px;
                        }

                        .meta {
                            width: 100%%;
                            margin-bottom: 22px;
                        }

                        .meta td {
                            padding: 4px 0;
                            vertical-align: top;
                        }

                        .label {
                            font-weight: bold;
                            color: #475569;
                        }

                        table.items {
                            width: 100%%;
                            border-collapse: collapse;
                            margin-top: 16px;
                        }

                        table.items th {
                            background: #26324f;
                            color: white;
                            padding: 9px;
                            text-align: left;
                        }

                        table.items td {
                            border-bottom: 1px solid #e5e7eb;
                            padding: 9px;
                        }

                        .center {
                            text-align: center;
                        }

                        .right {
                            text-align: right;
                        }

                        .totals {
                            width: 45%%;
                            margin-left: 55%%;
                            margin-top: 18px;
                            border-collapse: collapse;
                        }

                        .totals td {
                            padding: 6px;
                        }

                        .grand {
                            font-size: 15px;
                            font-weight: bold;
                            border-top: 2px solid #26324f;
                        }

                        .footer {
                            margin-top: 42px;
                            color: #64748b;
                            border-top: 1px solid #e5e7eb;
                            padding-top: 12px;
                        }
                    </style>
                </head>
                <body>

                    <div class="header">
                        <div class="clinic">SUNRISE DENTAL CLINIC</div>
                        <div class="subtitle">Dental Clinic Management System</div>
                    </div>

                    <div class="invoice-title">INVOICE</div>

                    <table class="meta">
                        <tr>
                            <td>
                                <span class="label">Invoice No:</span>
                                %s
                            </td>
                            <td>
                                <span class="label">Issue Date:</span>
                                %s
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <span class="label">Patient:</span>
                                %s (%s)
                            </td>
                            <td>
                                <span class="label">Appointment:</span>
                                %s
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <span class="label">Contact:</span>
                                %s
                            </td>
                            <td>
                                <span class="label">Dentist:</span>
                                %s
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <span class="label">Treatment Date:</span>
                                %s
                            </td>
                            <td>
                                <span class="label">Treatment:</span>
                                %s
                            </td>
                        </tr>
                    </table>

                    <table class="items">
                        <thead>
                            <tr>
                                <th>Description</th>
                                <th>Qty</th>
                                <th>Unit Price</th>
                                <th>Amount</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>

                    <table class="totals">
                        <tr>
                            <td>Subtotal</td>
                            <td class="right">%s</td>
                        </tr>
                        <tr>
                            <td>Consultation fee</td>
                            <td class="right">%s</td>
                        </tr>
                        <tr>
                            <td>Discount</td>
                            <td class="right">%s</td>
                        </tr>
                        <tr>
                            <td>Tax (%s%%)</td>
                            <td class="right">%s</td>
                        </tr>
                        <tr class="grand">
                            <td>Total</td>
                            <td class="right">%s</td>
                        </tr>
                    </table>

                    <div class="footer">
                        Thank you for choosing Sunrise Dental Clinic.
                    </div>

                </body>
                </html>
                """.formatted(
                safe(invoice.getInvoiceNo()),
                safe(String.valueOf(invoice.getIssueDate())),
                patientName,
                patientCode,
                safe(appointment.getAppointmentNo()),
                patientContact,
                dentistName,
                safe(String.valueOf(appointment.getAppointmentDate())),
                treatmentName,
                items,
                money(invoice.getSubTotal()),
                money(invoice.getConsultationFee()),
                money(invoice.getDiscount()),
                number(invoice.getTaxRate()),
                money(invoice.getTaxAmount()),
                money(invoice.getTotalAmount())
        );

        new PdfRendererBuilder()
                .useFastMode()
                .withHtmlContent(html, null)
                .toStream(output)
                .run();
    }

    private static String money(BigDecimal value) {
        return value == null ? "LKR 0.00" : "LKR " + value.setScale(2).toPlainString();
    }

    private static String number(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2).toPlainString();
    }

    private static String safe(String value) {
        if (value == null) return "—";

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

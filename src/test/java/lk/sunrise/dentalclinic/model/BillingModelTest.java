package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.*;
import lk.sunrise.dentalclinic.dto.InvoiceDTO;
import lk.sunrise.dentalclinic.dto.PaymentDTO;
import lk.sunrise.dentalclinic.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BillingModelTest {

    @Test
    void generateInvoiceForCompletedAppointmentCalculatesTotalsAndSaves() throws Exception {
        AppointmentDAO appointmentDAO = mock(AppointmentDAO.class);
        DentistDAO dentistDAO = mock(DentistDAO.class);
        TreatmentRecordDAO recordDAO = mock(TreatmentRecordDAO.class);
        InvoiceDAO invoiceDAO = mock(InvoiceDAO.class);
        BillingModel model = new BillingModel(appointmentDAO, dentistDAO, recordDAO, invoiceDAO, mock(PaymentDAO.class));
        Appointment appointment = appointment(AppointmentStatus.COMPLETED);
        Dentist dentist = appointment.getDentist();
        TreatmentRecord record = new TreatmentRecord(1, appointment.getPatient(), dentist, appointment.getTreatment(), appointment, LocalDate.of(2026, 8, 27), "Done", new BigDecimal("5000.00"));
        when(invoiceDAO.findByAppointmentId(20)).thenReturn(Optional.empty());
        when(appointmentDAO.findById(20)).thenReturn(Optional.of(appointment));
        when(recordDAO.findByAppointment(20)).thenReturn(List.of(record));
        when(dentistDAO.findById(2)).thenReturn(Optional.of(dentist));
        when(invoiceDAO.generateNextCode()).thenReturn("INV-000001");
        when(invoiceDAO.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setInvoiceId(30);
            return true;
        });
        when(invoiceDAO.findById(30)).thenReturn(Optional.empty());

        InvoiceDTO invoice = model.generateInvoice(20, new BigDecimal("10.00"), new BigDecimal("500.00"));

        assertEquals("INV-000001", invoice.getInvoiceNo());
        assertEquals(new BigDecimal("5000.00"), invoice.getSubTotal());
        assertEquals(new BigDecimal("2500.00"), invoice.getConsultationFee());
        assertEquals(new BigDecimal("700.0000"), invoice.getTaxAmount());
        assertEquals(new BigDecimal("7700.0000"), invoice.getTotalAmount());
        verify(invoiceDAO).save(any(Invoice.class));
    }

    @Test
    void generateInvoiceForScheduledAppointmentIsRejected() throws Exception {
        AppointmentDAO appointmentDAO = mock(AppointmentDAO.class);
        InvoiceDAO invoiceDAO = mock(InvoiceDAO.class);
        BillingModel model = new BillingModel(appointmentDAO, mock(DentistDAO.class), mock(TreatmentRecordDAO.class), invoiceDAO, mock(PaymentDAO.class));
        when(invoiceDAO.findByAppointmentId(20)).thenReturn(Optional.empty());
        when(appointmentDAO.findById(20)).thenReturn(Optional.of(appointment(AppointmentStatus.SCHEDULED)));

        assertThrows(IllegalArgumentException.class, () -> model.generateInvoice(20, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    void recordPaymentRejectsOverpayment() throws Exception {
        InvoiceDAO invoiceDAO = mock(InvoiceDAO.class);
        PaymentDAO paymentDAO = mock(PaymentDAO.class);
        BillingModel model = new BillingModel(mock(AppointmentDAO.class), mock(DentistDAO.class), mock(TreatmentRecordDAO.class), invoiceDAO, paymentDAO);
        Invoice invoice = invoice(new BigDecimal("1000.00"));
        when(invoiceDAO.findById(30)).thenReturn(Optional.of(invoice));
        when(paymentDAO.getTotalPaid(30)).thenReturn(new BigDecimal("900.00"));

        PaymentDTO payment = new PaymentDTO(0, 30, new BigDecimal("200.00"), null, PaymentMethod.CASH);

        assertThrows(IllegalArgumentException.class, () -> model.recordPayment(payment));
        verify(paymentDAO, never()).save(any(Payment.class));
    }

    @Test
    void recordPaymentWithValidAmountPersistsPaymentDate() throws Exception {
        InvoiceDAO invoiceDAO = mock(InvoiceDAO.class);
        PaymentDAO paymentDAO = mock(PaymentDAO.class);
        BillingModel model = new BillingModel(mock(AppointmentDAO.class), mock(DentistDAO.class), mock(TreatmentRecordDAO.class), invoiceDAO, paymentDAO);
        when(invoiceDAO.findById(30)).thenReturn(Optional.of(invoice(new BigDecimal("1000.00"))));
        when(paymentDAO.getTotalPaid(30)).thenReturn(new BigDecimal("100.00"));
        when(paymentDAO.save(any(Payment.class))).thenReturn(true);
        PaymentDTO payment = new PaymentDTO(0, 30, new BigDecimal("200.00"), null, PaymentMethod.CARD);

        PaymentDTO result = model.recordPayment(payment);

        assertNotNull(result.getPaymentDate());
        verify(paymentDAO).save(any(Payment.class));
    }

    private Appointment appointment(AppointmentStatus status) {
        Patient patient = new Patient(1, "PAT-000001", "Nimal Perera", null, Gender.MALE, "0712345678", null, null, null, null);
        Dentist dentist = new Dentist(2, "DEN-000001", "Dr Silva", "SLMC-1", null, null, null, new BigDecimal("2500.00"), LocalTime.of(9, 0), LocalTime.of(17, 0), true);
        Treatment treatment = new Treatment(3, "TRT-000001", "Cleaning", null, "Preventive", new BigDecimal("5000.00"), 30, true);
        return new Appointment(20, "APP-000020", patient, dentist, treatment, LocalDate.of(2026, 8, 27), LocalTime.of(10, 0), LocalTime.of(10, 30), status, null, null);
    }

    private Invoice invoice(BigDecimal total) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(30);
        invoice.setInvoiceNo("INV-000030");
        invoice.setTotalAmount(total);
        return invoice;
    }
}

package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.*;
import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.factory.DAOFactory;

import java.math.*;
import java.time.*;
import java.util.*;

public class BillingModel {
    private final AppointmentDAO appointmentDAO = DAOFactory.appointmentDAO();
    private final DentistDAO dentistDAO = DAOFactory.dentistDAO();
    private final TreatmentRecordDAO recordDAO = DAOFactory.treatmentRecordDAO();
    private final InvoiceDAO invoiceDAO = DAOFactory.invoiceDAO();
    private final PaymentDAO paymentDAO = DAOFactory.paymentDAO();

    public InvoiceDTO generateInvoice(int appointmentId, BigDecimal taxRate, BigDecimal discount) throws Exception {
        Invoice existing = invoiceDAO.findByAppointmentId(appointmentId).orElse(null);
        if (existing != null) return toDTO(existing);
        Appointment a = appointmentDAO.findById(appointmentId).orElseThrow(() -> new NoSuchElementException("Appointment not found."));
        if (a.getStatus() != AppointmentStatus.COMPLETED)
            throw new IllegalArgumentException("Only completed appointments can be invoiced.");
        List<TreatmentRecord> records = recordDAO.findByAppointment(appointmentId);
        if (records.isEmpty()) throw new IllegalArgumentException("Record the treatment first.");
        Dentist d = dentistDAO.findById(a.getDentist().getDentistId()).orElseThrow();
        Invoice i = new Invoice();
        i.setInvoiceNo(invoiceDAO.generateNextCode());
        i.setPatient(a.getPatient());
        i.setAppointment(a);
        i.setIssueDate(LocalDateTime.now());
        i.setConsultationFee(d.getConsultationFee());
        i.setTaxRate(taxRate == null ? BigDecimal.ZERO : taxRate);
        i.setDiscount(discount == null ? BigDecimal.ZERO : discount);
        i.setStatus(PaymentStatus.UNPAID);
        for (TreatmentRecord r : records) {
            InvoiceItem item = new InvoiceItem(0, r.getTreatment().getName(), 1, r.getChargedAmount(), r.getChargedAmount());
            i.getItems().add(item);
        }
        i.calculateTotal();
        invoiceDAO.save(i);
        return toDTO(invoiceDAO.findById(i.getInvoiceId()).orElse(i));
    }

    public InvoiceDTO findInvoice(int id) throws Exception {
        return toDTO(invoiceDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Invoice not found.")));
    }

    public PaymentDTO recordPayment(PaymentDTO x) throws Exception {
        Invoice i = invoiceDAO.findById(x.getInvoiceId()).orElseThrow(() -> new NoSuchElementException("Invoice not found."));
        BigDecimal paid = paymentDAO.getTotalPaid(i.getInvoiceId());
        if (x.getAmountPaid() == null || x.getAmountPaid().signum() <= 0)
            throw new IllegalArgumentException("Invalid payment amount.");
        if (paid.add(x.getAmountPaid()).compareTo(i.getTotalAmount()) > 0)
            throw new IllegalArgumentException("Payment exceeds invoice balance.");
        Payment p = new Payment(0, i, x.getAmountPaid(), LocalDateTime.now(), x.getMethod());
        paymentDAO.save(p);
        x.setPaymentDate(p.getPaymentDate());
        return x;
    }

    private InvoiceDTO toDTO(Invoice i) {
        List<InvoiceItemDTO> items = new ArrayList<>();
        for (InvoiceItem x : i.getItems())
            items.add(new InvoiceItemDTO(x.getItemId(), x.getQuantity(), x.getDescription(), x.getUnitPrice(), x.getLineTotal()));
        return new InvoiceDTO(i.getInvoiceId(), i.getInvoiceNo(), i.getPatient() == null ? 0 : i.getPatient().getPatientId(), i.getAppointment() == null ? 0 : i.getAppointment().getAppointmentId(), i.getIssueDate(), i.getSubTotal(), i.getConsultationFee(), i.getTaxRate(), i.getTaxAmount(), i.getDiscount(), i.getTotalAmount(), i.getStatus(), items);
    }
}

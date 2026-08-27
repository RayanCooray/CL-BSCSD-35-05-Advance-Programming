package lk.sunrise.dentalclinic.controller;

import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.model.BillingModel;
import java.math.*;

public class BillingController {
    private final BillingModel model=new BillingModel();
    public InvoiceDTO generateInvoice(int appointmentId,BigDecimal taxRate,BigDecimal discount)throws Exception {
        return model.generateInvoice(appointmentId,taxRate,discount);
    }
    public InvoiceDTO getInvoice(int id)throws Exception {
        return model.findInvoice(id);
    }
    public PaymentDTO recordPayment(PaymentDTO dto)throws Exception {
        return model.recordPayment(dto);
    }
}

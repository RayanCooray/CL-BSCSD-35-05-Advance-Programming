package lk.sunrise.dentalclinic.dao;

import java.util.*;

import java.math.*;

import lk.sunrise.dentalclinic.entity.*;

public interface PaymentDAO {
    boolean save(Payment payment) throws Exception;
    BigDecimal getTotalPaid(int invoiceId) throws Exception;
    List<Payment> findByInvoiceId(int invoiceId) throws Exception;
}

package lk.sunrise.dentalclinic.entity;

import lombok.*;
import java.math.*;
import java.time.*;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Invoice {
    private int invoiceId;
    private String invoiceNo;
    private Patient patient;
    private Appointment appointment;
    private LocalDateTime issueDate;
    private BigDecimal subTotal=BigDecimal.ZERO;
    private BigDecimal consultationFee=BigDecimal.ZERO;
    private BigDecimal taxRate=BigDecimal.ZERO;
    private BigDecimal taxAmount=BigDecimal.ZERO;
    private BigDecimal discount=BigDecimal.ZERO;
    private BigDecimal totalAmount=BigDecimal.ZERO;
    private PaymentStatus status=PaymentStatus.UNPAID;
    private List<InvoiceItem> items=new ArrayList<>();
    public void addItem(InvoiceItem item) {
        items.add(item);
        calculateTotal();
    }
    public BigDecimal calculateTotal() {
        subTotal=items.stream().map(InvoiceItem::calculateLineTotal).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal base=subTotal.add(consultationFee).subtract(discount).max(BigDecimal.ZERO);
        taxAmount=base.multiply(taxRate).divide(BigDecimal.valueOf(100));
        totalAmount=base.add(taxAmount);
        return totalAmount;
    }
}

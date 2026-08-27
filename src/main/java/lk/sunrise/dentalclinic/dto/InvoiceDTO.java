package lk.sunrise.dentalclinic.dto;

import lombok.*;
import java.math.*;
import java.time.*;
import java.util.*;
import lk.sunrise.dentalclinic.entity.PaymentStatus;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class InvoiceDTO {
    private int invoiceId;
    private String invoiceNo;
    private int patientId;
    private int appointmentId;
    private LocalDateTime issueDate;
    private BigDecimal subTotal;
    private BigDecimal consultationFee;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private PaymentStatus status;
    private List<InvoiceItemDTO> items=new ArrayList<>();
}

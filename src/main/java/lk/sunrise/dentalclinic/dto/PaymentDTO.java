package lk.sunrise.dentalclinic.dto;

import lombok.*;
import java.math.*;
import java.time.*;
import lk.sunrise.dentalclinic.entity.PaymentMethod;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class PaymentDTO {
    private int paymentId;
    private int invoiceId;
    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private PaymentMethod method;
}

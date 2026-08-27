package lk.sunrise.dentalclinic.entity;

import lombok.*;
import java.math.*;
import java.time.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Payment {
    private int paymentId;
    private Invoice invoice;
    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private PaymentMethod method;
}

package lk.sunrise.dentalclinic.dto;

import lombok.*;
import java.math.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class InvoiceItemDTO {
    private int itemId;
    private int quantity;
    private String description;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}

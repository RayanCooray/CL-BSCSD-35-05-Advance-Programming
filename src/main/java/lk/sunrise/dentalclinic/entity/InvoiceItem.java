package lk.sunrise.dentalclinic.entity;

import lombok.*;
import java.math.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class InvoiceItem {
    private int itemId;
    private String description;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    public BigDecimal calculateLineTotal() {
        return lineTotal=unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

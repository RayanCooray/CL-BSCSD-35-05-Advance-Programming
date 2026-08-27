package lk.sunrise.dentalclinic.dto;

import lombok.*;
import java.math.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class RevenueReportDTO {
    private int invoiceCount;
    private BigDecimal revenue;
    private BigDecimal tax;
    private BigDecimal collected;
    private BigDecimal outstanding;
}

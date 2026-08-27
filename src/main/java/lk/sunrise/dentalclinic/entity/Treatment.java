package lk.sunrise.dentalclinic.entity;

import lombok.*;
import java.math.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Treatment {
    private int treatmentId;
    private String treatmentCode;
    private String name;
    private String description;
    private String category;
    private BigDecimal basePrice;
    private int durationMinutes;
    private boolean active;
}

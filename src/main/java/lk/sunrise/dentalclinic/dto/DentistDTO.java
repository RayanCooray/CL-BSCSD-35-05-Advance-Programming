package lk.sunrise.dentalclinic.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class DentistDTO {
    private int dentistId;
    private String dentistCode;
    private String fullName;
    private String slmcNumber;
    private String specialization;
    private String contactNumber;
    private String email;
    private BigDecimal consultationFee;
    private LocalTime workingHoursStart;
    private LocalTime workingHoursEnd;
    private boolean available;
}

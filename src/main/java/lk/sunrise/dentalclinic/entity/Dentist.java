package lk.sunrise.dentalclinic.entity;

import lombok.*;
import java.math.*;
import java.time.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Dentist {
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
    public String getSlmcNumber() {
        return slmcNumber;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public boolean isAvailableAt(LocalTime time) {
        return available
                && time != null
                && workingHoursStart != null
                && workingHoursEnd != null
                && !time.isBefore(workingHoursStart)
                && !time.isAfter(workingHoursEnd);
    }
}

package lk.sunrise.dentalclinic.dto;

import lombok.*;
import java.math.*;
import java.time.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class TreatmentRecordDTO {
    private int recordId;
    private int patientId;
    private int dentistId;
    private int treatmentId;
    private int appointmentId;
    private LocalDate performedDate;
    private String treatmentName;
    private String dentistName;
    private String clinicalNotes;
    private BigDecimal chargedAmount;
}

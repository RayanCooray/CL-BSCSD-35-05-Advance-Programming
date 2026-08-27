package lk.sunrise.dentalclinic.entity;

import lombok.*;
import java.math.*;
import java.time.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class TreatmentRecord {
    private int recordId;
    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;
    private Appointment appointment;
    private LocalDate performedDate;
    private String clinicalNotes;
    private BigDecimal chargedAmount;
}

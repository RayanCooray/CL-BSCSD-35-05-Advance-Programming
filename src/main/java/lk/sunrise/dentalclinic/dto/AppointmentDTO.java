package lk.sunrise.dentalclinic.dto;

import lombok.*;
import java.time.*;
import lk.sunrise.dentalclinic.entity.AppointmentStatus;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class AppointmentDTO {
    private int appointmentId;
    private String appointmentNo;
    private int patientId;
    private int dentistId;
    private int treatmentId;
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private AppointmentStatus status;
    private String remarks;
}

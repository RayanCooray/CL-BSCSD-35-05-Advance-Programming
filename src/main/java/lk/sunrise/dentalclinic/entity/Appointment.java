package lk.sunrise.dentalclinic.entity;

import lombok.*;
import java.time.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Appointment {
    private int appointmentId;
    private String appointmentNo;
    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String remarks;
    private LocalDateTime createdAt;
}

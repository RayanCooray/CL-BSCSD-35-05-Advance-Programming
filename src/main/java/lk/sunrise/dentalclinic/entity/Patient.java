package lk.sunrise.dentalclinic.entity;

import lombok.*;
import java.time.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Patient {
    private int patientId;
    private String patientCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String contactNumber;
    private String email;
    private String address;
    private String medicalHistory;
    private LocalDateTime registeredAt;
}

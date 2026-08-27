package lk.sunrise.dentalclinic.dto;

import lombok.*;
import java.time.*;
import lk.sunrise.dentalclinic.entity.Gender;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class PatientDTO {
    private int patientId;
    private String patientCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String contactNumber;
    private String email;
    private String address;
    private String medicalHistory;
}

package lk.sunrise.dentalclinic.dto;

import lombok.*;
import java.time.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ReportRequestDTO {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer dentistId;
    private String outputFormat;
}

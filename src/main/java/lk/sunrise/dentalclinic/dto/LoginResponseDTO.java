package lk.sunrise.dentalclinic.dto;

import lombok.*;
import lk.sunrise.dentalclinic.entity.UserRole;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class LoginResponseDTO {
    private int userId;
    private String token;
    private String fullName;
    private UserRole role;
}

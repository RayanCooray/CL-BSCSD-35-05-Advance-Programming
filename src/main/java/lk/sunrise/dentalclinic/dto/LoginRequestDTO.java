package lk.sunrise.dentalclinic.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class LoginRequestDTO {
    private String username;
    private String password;
}

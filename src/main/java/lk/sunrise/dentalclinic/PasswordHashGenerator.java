package lk.sunrise.dentalclinic;

import lk.sunrise.dentalclinic.util.PasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        System.out.println(
                PasswordEncoder.encode("admin123")
        );
    }
}
package lk.sunrise.dentalclinic.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordEncoder {
    private PasswordEncoder() {
    }
    public static String encode(String v) {
        return BCrypt.hashpw(v,BCrypt.gensalt(12));
    }
    public static boolean matches(String v,String h) {
        return BCrypt.checkpw(v,h);
    }
}

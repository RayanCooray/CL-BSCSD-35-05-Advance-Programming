package lk.sunrise.dentalclinic.util;

import java.util.*;

public final class TokenUtil {
    private static final Set<String> BLACKLIST=Collections.synchronizedSet(new HashSet<>());
    private TokenUtil() {
    }
    public static String generate(int userId) {
        return userId+"."+UUID.randomUUID();
    }
    public static boolean isValid(String token) {
        return token!=null&&!token.isBlank()&&!BLACKLIST.contains(token);
    }
    public static void invalidate(String token) {
        if(token!=null)BLACKLIST.add(token);
    }
}

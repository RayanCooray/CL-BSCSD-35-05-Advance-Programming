package lk.sunrise.dentalclinic.ui.session;

import lk.sunrise.dentalclinic.dto.LoginResponseDTO;
import lk.sunrise.dentalclinic.entity.UserRole;

public final class SessionContext {
    private static final SessionContext INSTANCE = new SessionContext();
    private LoginResponseDTO user;

    private SessionContext() {
    }

    public static SessionContext getInstance() {
        return INSTANCE;
    }

    public void start(LoginResponseDTO response) {
        this.user = response;
    }

    public void clear() {
        this.user = null;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public LoginResponseDTO getUser() {
        return user;
    }

    public String getToken() {
        return user == null ? null : user.getToken();
    }

    public UserRole getRole() {
        return user == null ? null : user.getRole();
    }

    public String getFullName() {
        return user == null ? "" : user.getFullName();
    }

    public boolean hasRole(UserRole... roles) {
        if (user == null || user.getRole() == null) return false;
        for (UserRole role : roles) if (user.getRole() == role) return true;
        return false;
    }
}

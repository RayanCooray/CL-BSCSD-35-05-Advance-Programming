package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.UserDAO;
import lk.sunrise.dentalclinic.dto.LoginRequestDTO;
import lk.sunrise.dentalclinic.dto.LoginResponseDTO;
import lk.sunrise.dentalclinic.entity.User;
import lk.sunrise.dentalclinic.entity.UserRole;
import lk.sunrise.dentalclinic.util.PasswordEncoder;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthModelTest {

    @Test
    void authenticateWithValidCredentialsReturnsLoginResponse() throws Exception {
        UserDAO userDAO = mock(UserDAO.class);
        AuthModel model = new AuthModel(userDAO);
        User user = new User(1, "admin", PasswordEncoder.encode("secret"), "Admin User", "admin@clinic.test", UserRole.ADMIN, true);
        when(userDAO.findByUsername("admin")).thenReturn(Optional.of(user));

        LoginResponseDTO response = model.authenticate(new LoginRequestDTO("admin", "secret"));

        assertEquals(1, response.getUserId());
        assertEquals("Admin User", response.getFullName());
        assertEquals(UserRole.ADMIN, response.getRole());
        assertNotNull(response.getToken());
        assertFalse(response.getToken().isBlank());
    }

    @Test
    void authenticateWithBlankUsernameThrowsValidationError() {
        AuthModel model = new AuthModel(mock(UserDAO.class));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> model.authenticate(new LoginRequestDTO(" ", "secret"))
        );

        assertEquals("Username and password are required.", exception.getMessage());
    }

    @Test
    void authenticateWithWrongPasswordThrowsSecurityError() throws Exception {
        UserDAO userDAO = mock(UserDAO.class);
        AuthModel model = new AuthModel(userDAO);
        User user = new User(1, "admin", PasswordEncoder.encode("secret"), "Admin User", "admin@clinic.test", UserRole.ADMIN, true);
        when(userDAO.findByUsername("admin")).thenReturn(Optional.of(user));

        assertThrows(SecurityException.class, () -> model.authenticate(new LoginRequestDTO("admin", "wrong")));
    }

    @Test
    void registerWithDuplicateUsernameIsRejected() throws Exception {
        UserDAO userDAO = mock(UserDAO.class);
        AuthModel model = new AuthModel(userDAO);
        when(userDAO.existsByUsername("admin")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> model.register("admin", "secret", "Admin User", "admin@clinic.test", UserRole.ADMIN));
        verify(userDAO, never()).save(any(User.class));
    }
}

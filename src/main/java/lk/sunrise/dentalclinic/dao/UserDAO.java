package lk.sunrise.dentalclinic.dao;

import java.util.*;
import lk.sunrise.dentalclinic.entity.*;

public interface UserDAO {
    Optional<User> findByUsername(String username) throws Exception;
    boolean existsByUsername(String username) throws Exception;
    boolean save(User user) throws Exception;
}

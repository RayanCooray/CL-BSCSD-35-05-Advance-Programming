package lk.sunrise.dentalclinic.dao;

import java.util.*;
import java.sql.*;
import lk.sunrise.dentalclinic.entity.*;

public interface InvoiceDAO {
    Optional<Invoice> findById(int id) throws Exception;
    Optional<Invoice> findByAppointmentId(int appointmentId) throws Exception;
    String generateNextCode() throws Exception;
    boolean save(Invoice invoice) throws Exception;
    void saveWithItems(Connection connection,Invoice invoice) throws Exception;
}

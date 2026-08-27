package lk.sunrise.dentalclinic.dao;

import java.util.*;
import java.time.*;
import lk.sunrise.dentalclinic.entity.*;

public interface AppointmentDAO {
    boolean existsConflict(int dentistId, LocalDate date, LocalTime start, LocalTime end) throws Exception;
    boolean existsConflictExcept(int dentistId, LocalDate date, LocalTime start, LocalTime end, int appointmentId) throws Exception;

    String generateNextCode() throws Exception;

    boolean save(Appointment appointment) throws Exception;
    boolean update(Appointment appointment) throws Exception;

    List<Appointment> findByDateRange(LocalDate from, LocalDate to, Integer dentistId) throws Exception;

    Optional<Appointment> findById(int id) throws Exception;

    Optional<Appointment> findByAppointmentNo(String no) throws Exception;

    boolean updateStatus(int id, AppointmentStatus status) throws Exception;
}

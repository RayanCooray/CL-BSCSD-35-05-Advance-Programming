package lk.sunrise.dentalclinic.dao;

import java.util.*;

import lk.sunrise.dentalclinic.entity.*;

public interface PatientDAO {
    Optional<Patient> findById(int id) throws Exception;
    Optional<Patient> findByCode(String code) throws Exception;
    List<Patient> search(String keyword) throws Exception;
    boolean existsByContact(String contact) throws Exception;
    boolean existsByContactExcept(String contact, int patientId) throws Exception;
    String generateNextCode() throws Exception;
    boolean save(Patient patient) throws Exception;
    boolean update(Patient patient) throws Exception;
}

package lk.sunrise.dentalclinic.dao;

import lk.sunrise.dentalclinic.entity.Dentist;

import java.util.List;
import java.util.Optional;

public interface DentistDAO {
    Optional<Dentist> findById(int id) throws Exception;
    Optional<Dentist> findByCode(String code) throws Exception;
    Optional<Dentist> findBySlmcNumber(String slmcNumber) throws Exception;
    List<Dentist> findAll() throws Exception;
    List<Dentist> findAvailable() throws Exception;
    List<Dentist> search(String keyword) throws Exception;
    String generateNextCode() throws Exception;
    boolean save(Dentist dentist) throws Exception;
    boolean update(Dentist dentist) throws Exception;
}

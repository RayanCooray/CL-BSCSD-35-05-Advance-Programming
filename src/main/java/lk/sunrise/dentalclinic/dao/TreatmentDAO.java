package lk.sunrise.dentalclinic.dao;

import lk.sunrise.dentalclinic.entity.Treatment;

import java.util.List;
import java.util.Optional;

public interface TreatmentDAO {
    Optional<Treatment> findById(int id) throws Exception;

    List<Treatment> findAll() throws Exception;

    List<Treatment> findActive() throws Exception;

    boolean existsByName(String name, int excludeId) throws Exception;

    String generateNextCode() throws Exception;

    boolean save(Treatment treatment) throws Exception;

    boolean update(Treatment treatment) throws Exception;

    boolean delete(int id) throws Exception;
}

package lk.sunrise.dentalclinic.dao;

import java.util.*;
import lk.sunrise.dentalclinic.entity.*;

public interface TreatmentRecordDAO {
    List<TreatmentRecord> findByPatient(int patientId) throws Exception;
    List<TreatmentRecord> findByAppointment(int appointmentId) throws Exception;
    boolean save(TreatmentRecord record) throws Exception;
}

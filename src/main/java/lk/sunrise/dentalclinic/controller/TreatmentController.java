package lk.sunrise.dentalclinic.controller;

import lk.sunrise.dentalclinic.entity.Treatment;
import lk.sunrise.dentalclinic.entity.TreatmentRecord;
import lk.sunrise.dentalclinic.model.TreatmentModel;

import java.math.BigDecimal;
import java.util.List;

public class TreatmentController {
    private final TreatmentModel model = new TreatmentModel();

    public List<Treatment> getAll() throws Exception {
        return model.findAll();
    }

    public List<Treatment> getActive() throws Exception {
        return model.findActive();
    }

    public Treatment getById(int id) throws Exception {
        return model.findById(id);
    }

    public Treatment create(String name,
                            String description,
                            String category,
                            BigDecimal basePrice,
                            int durationMinutes,
                            boolean active) throws Exception {
        return model.create(name, description, category, basePrice, durationMinutes, active);
    }

    public Treatment update(int id,
                            String name,
                            String description,
                            String category,
                            BigDecimal basePrice,
                            int durationMinutes,
                            boolean active) throws Exception {
        return model.update(id, name, description, category, basePrice, durationMinutes, active);
    }

    public void delete(int id) throws Exception {
        model.delete(id);
    }

    public List<TreatmentRecord> getHistory(String patientCode) throws Exception {
        return model.history(patientCode);
    }

    public List<TreatmentRecord> getByAppointment(int appointmentId) throws Exception {
        return model.byAppointment(appointmentId);
    }

    public boolean save(TreatmentRecord record) throws Exception {
        return model.save(record);
    }
}

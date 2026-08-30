package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.PatientDAO;
import lk.sunrise.dentalclinic.dao.TreatmentDAO;
import lk.sunrise.dentalclinic.dao.TreatmentRecordDAO;
import lk.sunrise.dentalclinic.entity.Patient;
import lk.sunrise.dentalclinic.entity.Treatment;
import lk.sunrise.dentalclinic.entity.TreatmentRecord;
import lk.sunrise.dentalclinic.factory.DAOFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

public class TreatmentModel {
    private final TreatmentDAO dao;
    private final PatientDAO patientDAO;
    private final TreatmentRecordDAO recordDAO;

    public TreatmentModel() {
        this(DAOFactory.treatmentDAO(), DAOFactory.patientDAO(), DAOFactory.treatmentRecordDAO());
    }

    TreatmentModel(TreatmentDAO dao, PatientDAO patientDAO, TreatmentRecordDAO recordDAO) {
        this.dao = dao;
        this.patientDAO = patientDAO;
        this.recordDAO = recordDAO;
    }

    public List<Treatment> findAll() throws Exception {
        return dao.findAll();
    }

    public List<Treatment> findActive() throws Exception {
        return dao.findActive();
    }

    public Treatment findById(int id) throws Exception {
        return dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Treatment not found."));
    }

    public Treatment create(String name,
                            String description,
                            String category,
                            BigDecimal basePrice,
                            int durationMinutes,
                            boolean active) throws Exception {
        validate(name, category, basePrice, durationMinutes);

        if (dao.existsByName(name, 0)) {
            throw new IllegalArgumentException("A treatment with this name already exists.");
        }

        Treatment treatment = new Treatment();
        treatment.setTreatmentCode(dao.generateNextCode());
        treatment.setName(name.trim());
        treatment.setDescription(blankToNull(description));
        treatment.setCategory(blankToNull(category));
        treatment.setBasePrice(basePrice);
        treatment.setDurationMinutes(durationMinutes);
        treatment.setActive(active);

        if (!dao.save(treatment)) {
            throw new IllegalStateException("Treatment could not be saved.");
        }

        return treatment;
    }

    public Treatment update(int id,
                            String name,
                            String description,
                            String category,
                            BigDecimal basePrice,
                            int durationMinutes,
                            boolean active) throws Exception {
        validate(name, category, basePrice, durationMinutes);

        Treatment treatment = findById(id);

        if (dao.existsByName(name, id)) {
            throw new IllegalArgumentException("A treatment with this name already exists.");
        }

        treatment.setName(name.trim());
        treatment.setDescription(blankToNull(description));
        treatment.setCategory(blankToNull(category));
        treatment.setBasePrice(basePrice);
        treatment.setDurationMinutes(durationMinutes);
        treatment.setActive(active);

        if (!dao.update(treatment)) {
            throw new IllegalStateException("Treatment could not be updated.");
        }

        return treatment;
    }

    public void delete(int id) throws Exception {
        findById(id);

        try {
            if (!dao.delete(id)) {
                throw new IllegalStateException("Treatment could not be deleted.");
            }
        } catch (Exception e) {
            String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (message.contains("foreign key") || message.contains("constraint") || message.contains("1451")) {
                throw new IllegalStateException(
                        "This treatment is already used by appointments or treatment records. " +
                                "Deactivate it instead of deleting it.");
            }
            throw e;
        }
    }

    private void validate(String name,
                          String category,
                          BigDecimal basePrice,
                          int durationMinutes) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Treatment name is required.");
        }

        if (!name.trim().matches("^[A-Za-z0-9][A-Za-z0-9 &()/'-]{2,149}$")) {
            throw new IllegalArgumentException("Treatment name contains invalid characters.");
        }

        if (category != null && !category.isBlank() &&
                !category.trim().matches("^[A-Za-z0-9][A-Za-z0-9 &()/'-]{1,99}$")) {
            throw new IllegalArgumentException("Treatment category contains invalid characters.");
        }

        if (basePrice == null || basePrice.signum() < 0) {
            throw new IllegalArgumentException("Treatment price must be zero or greater.");
        }

        if (durationMinutes <= 0 || durationMinutes > 1440) {
            throw new IllegalArgumentException("Duration must be between 1 and 1440 minutes.");
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public List<TreatmentRecord> history(String code) throws Exception {
        Patient p = patientDAO.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Patient record not found."));
        return recordDAO.findByPatient(p.getPatientId());
    }

    public List<TreatmentRecord> byAppointment(int appointmentId) throws Exception {
        return recordDAO.findByAppointment(appointmentId);
    }

    public boolean save(TreatmentRecord x) throws Exception {
        if (x == null
                || x.getPatient() == null
                || x.getDentist() == null
                || x.getTreatment() == null
                || x.getAppointment() == null) {
            throw new IllegalArgumentException(
                    "Patient, dentist, treatment and appointment are required."
            );
        }

        if (x.getAppointment().getStatus() != lk.sunrise.dentalclinic.entity.AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Treatment can only be recorded for a completed appointment."
            );
        }

        if (x.getPerformedDate() == null) {
            throw new IllegalArgumentException("Performed date is required.");
        }

        if (x.getChargedAmount() == null || x.getChargedAmount().signum() < 0) {
            throw new IllegalArgumentException("Charged amount cannot be negative.");
        }

        if (!recordDAO.findByAppointment(
                x.getAppointment().getAppointmentId()
        ).isEmpty()) {
            throw new IllegalArgumentException(
                    "Treatment has already been recorded for this appointment."
            );
        }

        return recordDAO.save(x);
    }
}

package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.DentistDAO;
import lk.sunrise.dentalclinic.dto.DentistDTO;
import lk.sunrise.dentalclinic.entity.Dentist;
import lk.sunrise.dentalclinic.factory.DAOFactory;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

public class DentistModel {

    private final DentistDAO dao = DAOFactory.dentistDAO();

    public DentistDTO register(DentistDTO x) throws Exception {
        validate(x);

        if (dao.findBySlmcNumber(x.getSlmcNumber()).isPresent()) {
            throw new IllegalArgumentException("SLMC number already exists.");
        }

        Dentist d = new Dentist();
        d.setDentistCode(dao.generateNextCode());
        d.setFullName(x.getFullName().trim());
        d.setSlmcNumber(x.getSlmcNumber().trim());
        d.setSpecialization(x.getSpecialization());
        d.setContactNumber(x.getContactNumber());
        d.setEmail(x.getEmail());
        d.setConsultationFee(x.getConsultationFee());
        d.setWorkingHoursStart(x.getWorkingHoursStart());
        d.setWorkingHoursEnd(x.getWorkingHoursEnd());
        d.setAvailable(x.isAvailable());

        if (!dao.save(d)) {
            throw new IllegalStateException("Dentist could not be saved.");
        }

        return toDTO(d);
    }

    public DentistDTO update(DentistDTO x) throws Exception {
        if (x == null || x.getDentistId() <= 0) {
            throw new IllegalArgumentException("Valid dentist is required.");
        }
        validate(x);

        Dentist existing = dao.findById(x.getDentistId())
                .orElseThrow(() -> new NoSuchElementException("Dentist not found."));

        dao.findBySlmcNumber(x.getSlmcNumber()).ifPresent(found -> {
            if (found.getDentistId() != existing.getDentistId()) {
                throw new IllegalArgumentException("SLMC number already exists.");
            }
        });

        existing.setFullName(x.getFullName().trim());
        existing.setSlmcNumber(x.getSlmcNumber().trim());
        existing.setSpecialization(x.getSpecialization());
        existing.setContactNumber(x.getContactNumber());
        existing.setEmail(x.getEmail());
        existing.setConsultationFee(x.getConsultationFee());
        existing.setWorkingHoursStart(x.getWorkingHoursStart());
        existing.setWorkingHoursEnd(x.getWorkingHoursEnd());
        existing.setAvailable(x.isAvailable());

        if (!dao.update(existing)) {
            throw new IllegalStateException("Dentist could not be updated.");
        }

        return toDTO(existing);
    }

    public Dentist findById(int id) throws Exception {
        return dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Dentist not found."));
    }

    public Dentist findByCode(String code) throws Exception {
        return dao.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Dentist not found."));
    }

    public List<Dentist> findAll() throws Exception {
        return dao.findAll();
    }

    public List<Dentist> findAvailable() throws Exception {
        return dao.findAvailable();
    }

    public List<Dentist> search(String keyword) throws Exception {
        return dao.search(keyword);
    }

    private void validate(DentistDTO x) {
        if (x == null) throw new IllegalArgumentException("Dentist details are required.");
        if (x.getFullName() == null || x.getFullName().isBlank()) {
            throw new IllegalArgumentException("Dentist name is required.");
        }
        if (x.getSlmcNumber() == null || x.getSlmcNumber().isBlank()) {
            throw new IllegalArgumentException("SLMC number is required.");
        }
        if (x.getConsultationFee() == null || x.getConsultationFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Consultation fee must be zero or greater.");
        }
        LocalTime start = x.getWorkingHoursStart();
        LocalTime end = x.getWorkingHoursEnd();
        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("Invalid dentist working hours.");
        }
    }

    private DentistDTO toDTO(Dentist d) {
        return new DentistDTO(
                d.getDentistId(),
                d.getDentistCode(),
                d.getFullName(),
                d.getSlmcNumber(),
                d.getSpecialization(),
                d.getContactNumber(),
                d.getEmail(),
                d.getConsultationFee(),
                d.getWorkingHoursStart(),
                d.getWorkingHoursEnd(),
                d.isAvailable()
        );
    }
}

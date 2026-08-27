package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.*;
import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.factory.DAOFactory;

import java.util.*;

public class PatientModel {
    private final PatientDAO dao = DAOFactory.patientDAO();

    public PatientDTO register(PatientDTO x) throws Exception {
        if (x == null || x.getFullName() == null || x.getFullName().isBlank())
            throw new IllegalArgumentException("Patient name is required.");
        if (dao.existsByContact(x.getContactNumber()))
            throw new IllegalArgumentException("Patient already registered.");
        Patient p = new Patient();
        p.setPatientCode(dao.generateNextCode());
        p.setFullName(x.getFullName());
        p.setDateOfBirth(x.getDateOfBirth());
        p.setGender(x.getGender());
        p.setContactNumber(x.getContactNumber());
        p.setEmail(x.getEmail());
        p.setAddress(x.getAddress());
        p.setMedicalHistory(x.getMedicalHistory());
        if (!dao.save(p)) throw new IllegalStateException("Patient could not be saved.");
        return new PatientDTO(p.getPatientId(), p.getPatientCode(), p.getFullName(), p.getDateOfBirth(), p.getGender(), p.getContactNumber(), p.getEmail(), p.getAddress(), p.getMedicalHistory());
    }

    public PatientDTO update(PatientDTO x) throws Exception {
        if (x == null || x.getPatientId() <= 0) throw new IllegalArgumentException("Select a patient first.");
        if (x.getFullName() == null || x.getFullName().isBlank())
            throw new IllegalArgumentException("Patient name is required.");
        if (dao.existsByContactExcept(x.getContactNumber(), x.getPatientId()))
            throw new IllegalArgumentException("Another patient already uses this contact number.");
        Patient p = dao.findById(x.getPatientId()).orElseThrow(() -> new NoSuchElementException("Patient record not found."));
        p.setFullName(x.getFullName());
        p.setDateOfBirth(x.getDateOfBirth());
        p.setGender(x.getGender());
        p.setContactNumber(x.getContactNumber());
        p.setEmail(x.getEmail());
        p.setAddress(x.getAddress());
        p.setMedicalHistory(x.getMedicalHistory());
        if (!dao.update(p)) throw new IllegalStateException("Patient could not be updated.");
        return new PatientDTO(p.getPatientId(), p.getPatientCode(), p.getFullName(), p.getDateOfBirth(), p.getGender(), p.getContactNumber(), p.getEmail(), p.getAddress(), p.getMedicalHistory());
    }

    public Patient findById(int id) throws Exception {
        return dao.findById(id).orElseThrow(() -> new NoSuchElementException("Patient record not found."));
    }

    public List<Patient> search(String key) throws Exception {
        return dao.search(key);
    }

    public Patient findByCode(String code) throws Exception {
        return dao.findByCode(code).orElseThrow(() -> new NoSuchElementException("Patient record not found."));
    }

    public List<Patient> findAllByKeyword(String key) throws Exception {
        return dao.search(key);
    }
}

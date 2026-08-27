package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.*;
import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.factory.DAOFactory;

import java.time.*;
import java.util.*;

public class AppointmentModel {
    private final AppointmentDAO appointmentDAO = DAOFactory.appointmentDAO();
    private final PatientDAO patientDAO = DAOFactory.patientDAO();
    private final DentistDAO dentistDAO = DAOFactory.dentistDAO();
    private final TreatmentDAO treatmentDAO = DAOFactory.treatmentDAO();

    public List<Dentist> getAvailableDentists() throws Exception {
        return dentistDAO.findAvailable();
    }

    public List<Treatment> getActiveTreatments() throws Exception {
        return treatmentDAO.findActive();
    }

    public AppointmentDTO create(AppointmentDTO x) throws Exception {
        if (x == null || x.getDate() == null || x.getStart() == null || x.getEnd() == null)
            throw new IllegalArgumentException("Appointment date and time are required.");
        Patient p = patientDAO.findById(x.getPatientId()).orElseThrow(() -> new NoSuchElementException("Patient not found."));
        Dentist d = dentistDAO.findById(x.getDentistId()).orElseThrow(() -> new NoSuchElementException("Dentist not found."));
        Treatment t = treatmentDAO.findById(x.getTreatmentId()).orElseThrow(() -> new NoSuchElementException("Treatment not found."));
        if (!d.isAvailableAt(x.getStart())) throw new IllegalArgumentException("Outside dentist working hours.");
        if (x.getEnd() == null || !x.getEnd().isAfter(x.getStart()))
            throw new IllegalArgumentException("Invalid appointment time.");
        if (appointmentDAO.existsConflict(x.getDentistId(), x.getDate(), x.getStart(), x.getEnd()))
            throw new IllegalArgumentException("Dentist is already booked at this time.");
        Appointment a = new Appointment();
        a.setAppointmentNo(appointmentDAO.generateNextCode());
        a.setPatient(p);
        a.setDentist(d);
        a.setTreatment(t);
        a.setAppointmentDate(x.getDate());
        a.setStartTime(x.getStart());
        a.setEndTime(x.getEnd());
        a.setStatus(AppointmentStatus.SCHEDULED);
        a.setRemarks(x.getRemarks());
        if (!appointmentDAO.save(a))
            throw new IllegalArgumentException("Dentist is already booked at this time. Please retry with an available slot.");
        x.setAppointmentNo(a.getAppointmentNo());
        x.setStatus(a.getStatus());
        return x;
    }

    public AppointmentDTO update(AppointmentDTO x) throws Exception {
        if (x == null || x.getAppointmentId() <= 0) throw new IllegalArgumentException("Select an appointment first.");
        if (x.getDate() == null || x.getStart() == null || x.getEnd() == null)
            throw new IllegalArgumentException("Appointment date and time are required.");
        if (!x.getEnd().isAfter(x.getStart())) throw new IllegalArgumentException("End time must be after start time.");
        Patient p = patientDAO.findById(x.getPatientId()).orElseThrow(() -> new NoSuchElementException("Patient not found."));
        Dentist d = dentistDAO.findById(x.getDentistId()).orElseThrow(() -> new NoSuchElementException("Dentist not found."));
        Treatment t = treatmentDAO.findById(x.getTreatmentId()).orElseThrow(() -> new NoSuchElementException("Treatment not found."));
        if (!d.isAvailableAt(x.getStart())) throw new IllegalArgumentException("Outside dentist working hours.");
        if (appointmentDAO.existsConflictExcept(x.getDentistId(), x.getDate(), x.getStart(), x.getEnd(), x.getAppointmentId()))
            throw new IllegalArgumentException("Dentist is already booked at this time.");
        Appointment a = appointmentDAO.findById(x.getAppointmentId()).orElseThrow(() -> new NoSuchElementException("Appointment not found."));
        a.setPatient(p);
        a.setDentist(d);
        a.setTreatment(t);
        a.setAppointmentDate(x.getDate());
        a.setStartTime(x.getStart());
        a.setEndTime(x.getEnd());
        a.setStatus(x.getStatus() == null ? AppointmentStatus.SCHEDULED : x.getStatus());
        a.setRemarks(x.getRemarks());
        if (!appointmentDAO.update(a)) throw new IllegalStateException("Appointment could not be updated.");
        x.setAppointmentNo(a.getAppointmentNo());
        x.setStatus(a.getStatus());
        return x;
    }

    public List<Appointment> daily(LocalDate from, LocalDate to, Integer dentistId) throws Exception {
        return appointmentDAO.findByDateRange(from, to, dentistId);
    }

    public Appointment find(int id) throws Exception {
        return appointmentDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Appointment not found."));
    }

    public boolean updateStatus(int id, AppointmentStatus s) throws Exception {
        return appointmentDAO.updateStatus(id, s);
    }
}

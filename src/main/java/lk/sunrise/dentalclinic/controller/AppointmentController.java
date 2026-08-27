package lk.sunrise.dentalclinic.controller;

import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.model.AppointmentModel;
import java.time.*;
import java.util.*;

public class AppointmentController {
    private final AppointmentModel model=new AppointmentModel();
    public List<Dentist> getAvailableDentists()throws Exception {
        return model.getAvailableDentists();
    }
    public List<Treatment> getActiveTreatments()throws Exception {
        return model.getActiveTreatments();
    }
    public AppointmentDTO update(AppointmentDTO dto)throws Exception {
        return model.update(dto);
    }

    public AppointmentDTO create(AppointmentDTO dto)throws Exception {
        return model.create(dto);
    }
    public List<Appointment> daily(LocalDate from,LocalDate to,Integer dentistId)throws Exception {
        return model.daily(from,to,dentistId);
    }
    public Appointment getById(int id)throws Exception {
        return model.find(id);
    }
    public boolean updateStatus(int id,AppointmentStatus status)throws Exception {
        return model.updateStatus(id,status);
    }
}

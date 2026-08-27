package lk.sunrise.dentalclinic.controller;

import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.Patient;
import lk.sunrise.dentalclinic.model.PatientModel;
import java.util.*;

public class PatientController {
    private final PatientModel model=new PatientModel();
    public PatientDTO register(PatientDTO dto)throws Exception {
        return model.register(dto);
    }
    public PatientDTO update(PatientDTO dto)throws Exception {
        return model.update(dto);
    }

    public Patient getById(int id)throws Exception {
        return model.findById(id);
    }

    public List<Patient> search(String keyword)throws Exception {
        return model.search(keyword);
    }
    public Patient getByCode(String code)throws Exception {
        return model.findByCode(code);
    }
}

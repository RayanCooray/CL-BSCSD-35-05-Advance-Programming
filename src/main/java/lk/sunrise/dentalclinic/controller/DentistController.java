package lk.sunrise.dentalclinic.controller;

import lk.sunrise.dentalclinic.dto.DentistDTO;
import lk.sunrise.dentalclinic.entity.Dentist;
import lk.sunrise.dentalclinic.model.DentistModel;

import java.util.List;

public class DentistController {

    private final DentistModel model = new DentistModel();

    public DentistDTO register(DentistDTO dto) throws Exception {
        return model.register(dto);
    }

    public DentistDTO update(DentistDTO dto) throws Exception {
        return model.update(dto);
    }

    public Dentist getById(int id) throws Exception {
        return model.findById(id);
    }

    public Dentist getByCode(String code) throws Exception {
        return model.findByCode(code);
    }

    public List<Dentist> findAll() throws Exception {
        return model.findAll();
    }

    public List<Dentist> findAvailable() throws Exception {
        return model.findAvailable();
    }

    public List<Dentist> search(String keyword) throws Exception {
        return model.search(keyword);
    }
}

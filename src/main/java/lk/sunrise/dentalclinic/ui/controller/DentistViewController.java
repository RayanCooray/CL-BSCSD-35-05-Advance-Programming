package lk.sunrise.dentalclinic.ui.controller;

import lk.sunrise.dentalclinic.controller.DentistController;
import lk.sunrise.dentalclinic.dto.DentistDTO;
import lk.sunrise.dentalclinic.entity.Dentist;
import lk.sunrise.dentalclinic.ui.util.Ui;
import lk.sunrise.dentalclinic.ui.view.DentistView;

import java.math.BigDecimal;
import java.time.LocalTime;

public class DentistViewController {
    private final DentistView view;
    private final DentistController controller = new DentistController();

    public DentistViewController(DentistView v) {
        view = v;
    }

    public void initialize() {
        view.save().setOnAction(e -> save());
        view.update().setOnAction(e -> update());
        search();
    }

    public void search() {
        try {
            view.table().getItems().setAll(controller.search(view.search().getText()));
        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    public void loadSelected() {
        Dentist d = view.table().getSelectionModel().getSelectedItem();
        if (d == null) return;
        view.name().setText(d.getFullName());
        view.slmc().setText(d.getSlmcNumber());
        view.special().setText(d.getSpecialization());
        view.contact().setText(d.getContactNumber());
        view.email().setText(d.getEmail());
        view.fee().setText(d.getConsultationFee().toPlainString());
        view.start().setText(d.getWorkingHoursStart().toString());
        view.end().setText(d.getWorkingHoursEnd().toString());
        view.available().setSelected(d.isAvailable());
    }

    private DentistDTO dto(int id) {
        return new DentistDTO(id, null, view.name().getText(), view.slmc().getText(), view.special().getText(), view.contact().getText(), view.email().getText(), new BigDecimal(view.fee().getText()), LocalTime.parse(view.start().getText()), LocalTime.parse(view.end().getText()), view.available().isSelected());
    }

    public void save() {
        try {
            DentistDTO d = controller.register(dto(0));
            search();
            Ui.notify(view.root(), "Dentist added", d.getDentistCode() + " was saved.", false);
        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }

    public void update() {
        try {
            Dentist selected = view.table().getSelectionModel().getSelectedItem();
            if (selected == null) throw new IllegalArgumentException("Select a dentist first.");
            controller.update(dto(selected.getDentistId()));
            search();
            Ui.notify(view.root(), "Dentist updated", "Changes saved.", false);
        } catch (Exception e) {
            Ui.error(view.root(), e);
        }
    }
}

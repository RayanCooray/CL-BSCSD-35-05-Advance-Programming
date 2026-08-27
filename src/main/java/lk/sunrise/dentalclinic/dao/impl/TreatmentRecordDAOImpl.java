package lk.sunrise.dentalclinic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lk.sunrise.dentalclinic.dao.TreatmentRecordDAO;
import lk.sunrise.dentalclinic.entity.Appointment;
import lk.sunrise.dentalclinic.entity.Dentist;
import lk.sunrise.dentalclinic.entity.Patient;
import lk.sunrise.dentalclinic.entity.Treatment;
import lk.sunrise.dentalclinic.entity.TreatmentRecord;

public class TreatmentRecordDAOImpl extends BaseDAOImpl implements TreatmentRecordDAO {

    private TreatmentRecord map(ResultSet r) throws Exception {

        Patient p = new Patient();
        p.setPatientId(r.getInt("patient_id"));
        p.setPatientCode(r.getString("patient_code"));
        p.setFullName(r.getString("patient_name"));

        Dentist d = new Dentist();
        d.setDentistId(r.getInt("dentist_id"));
        d.setFullName(r.getString("dentist_name"));

        Treatment t = new Treatment();
        t.setTreatmentId(r.getInt("treatment_id"));
        t.setName(r.getString("treatment_name"));

        Appointment a = new Appointment();
        a.setAppointmentId(r.getInt("appointment_id"));
        a.setAppointmentNo(r.getString("appointment_no"));

        TreatmentRecord x = new TreatmentRecord();

        x.setRecordId(r.getInt("record_id"));
        x.setPatient(p);
        x.setDentist(d);
        x.setTreatment(t);
        x.setAppointment(a);
        x.setPerformedDate(date(r, "performed_date"));
        x.setClinicalNotes(r.getString("clinical_notes"));
        x.setChargedAmount(r.getBigDecimal("charged_amount"));

        return x;
    }

    private String q() {

        return "SELECT tr.*, " +
                "p.patient_code, " +
                "p.full_name AS patient_name, " +
                "d.full_name AS dentist_name, " +
                "t.name AS treatment_name, " +
                "a.appointment_no " +
                "FROM treatment_record tr " +
                "JOIN patients p ON p.patient_id = tr.patient_id " +
                "JOIN dentists d ON d.dentist_id = tr.dentist_id " +
                "JOIN treatments t ON t.treatment_id = tr.treatment_id " +
                "JOIN appointments a ON a.appointment_id = tr.appointment_id ";
    }

    private List<TreatmentRecord> list(String sql, Object value) throws Exception {

        List<TreatmentRecord> records = new ArrayList<>();

        try (
                Connection c = connection();
                PreparedStatement p = c.prepareStatement(sql)
        ) {

            p.setObject(1, value);

            try (ResultSet r = p.executeQuery()) {

                while (r.next()) {
                    records.add(map(r));
                }
            }
        }

        return records;
    }

    @Override
    public List<TreatmentRecord> findByPatient(int id) throws Exception {

        return list(
                q() +
                        "WHERE tr.patient_id = ? " +
                        "ORDER BY tr.performed_date DESC",
                id
        );
    }

    @Override
    public List<TreatmentRecord> findByAppointment(int id) throws Exception {

        return list(
                q() +
                        "WHERE tr.appointment_id = ? " +
                        "ORDER BY tr.record_id",
                id
        );
    }

    @Override
    public boolean save(TreatmentRecord x) throws Exception {

        String sql =
                "INSERT INTO treatment_record " +
                        "(patient_id, dentist_id, treatment_id, appointment_id, " +
                        "performed_date, clinical_notes, charged_amount) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection c = connection();
                PreparedStatement p = c.prepareStatement(sql)
        ) {

            p.setInt(
                    1,
                    x.getPatient().getPatientId()
            );

            p.setInt(
                    2,
                    x.getDentist().getDentistId()
            );

            p.setInt(
                    3,
                    x.getTreatment().getTreatmentId()
            );

            p.setInt(
                    4,
                    x.getAppointment().getAppointmentId()
            );

            p.setDate(
                    5,
                    Date.valueOf(x.getPerformedDate())
            );

            p.setString(
                    6,
                    x.getClinicalNotes()
            );

            p.setBigDecimal(
                    7,
                    x.getChargedAmount()
            );

            return p.executeUpdate() == 1;
        }
    }
}
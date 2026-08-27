package lk.sunrise.dentalclinic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lk.sunrise.dentalclinic.dao.AppointmentDAO;
import lk.sunrise.dentalclinic.entity.Appointment;
import lk.sunrise.dentalclinic.entity.AppointmentStatus;
import lk.sunrise.dentalclinic.entity.Dentist;
import lk.sunrise.dentalclinic.entity.Patient;
import lk.sunrise.dentalclinic.entity.Treatment;

public class AppointmentDAOImpl extends BaseDAOImpl implements AppointmentDAO {

    private Appointment map(ResultSet r) throws Exception {

        Patient p = new Patient();
        p.setPatientId(r.getInt("patient_id"));
        p.setPatientCode(r.getString("patient_code"));
        p.setFullName(r.getString("patient_name"));

        Dentist d = new Dentist();
        d.setDentistId(r.getInt("dentist_id"));
        d.setDentistCode(r.getString("dentist_code"));
        d.setFullName(r.getString("dentist_name"));
        d.setConsultationFee(r.getBigDecimal("consultation_fee"));

        Treatment t = new Treatment();
        t.setTreatmentId(r.getInt("treatment_id"));
        t.setTreatmentCode(r.getString("treatment_code"));
        t.setName(r.getString("treatment_name"));
        t.setBasePrice(r.getBigDecimal("base_price"));

        Appointment a = new Appointment();
        a.setAppointmentId(r.getInt("appointment_id"));
        a.setAppointmentNo(r.getString("appointment_no"));
        a.setPatient(p);
        a.setDentist(d);
        a.setTreatment(t);
        a.setAppointmentDate(date(r, "appointment_date"));
        a.setStartTime(time(r, "start_time"));
        a.setEndTime(time(r, "end_time"));
        a.setStatus(
                AppointmentStatus.valueOf(
                        r.getString("status")
                )
        );
        a.setRemarks(r.getString("remarks"));
        a.setCreatedAt(timestamp(r, "created_at"));

        return a;
    }

    private String base() {

        return "SELECT a.*, " +
                "p.patient_code, " +
                "p.full_name AS patient_name, " +
                "d.dentist_code, " +
                "d.full_name AS dentist_name, " +
                "d.consultation_fee, " +
                "t.treatment_code, " +
                "t.name AS treatment_name, " +
                "t.base_price " +
                "FROM appointments a " +
                "JOIN patients p ON p.patient_id = a.patient_id " +
                "JOIN dentists d ON d.dentist_id = a.dentist_id " +
                "JOIN treatments t ON t.treatment_id = a.treatment_id ";
    }

    @Override
    public boolean existsConflict(
            int dentistId,
            LocalDate date,
            LocalTime start,
            LocalTime end
    ) throws Exception {

        String q =
                "SELECT COUNT(*) " +
                        "FROM appointments " +
                        "WHERE dentist_id = ? " +
                        "AND appointment_date = ? " +
                        "AND status NOT IN ('CANCELLED', 'NO_SHOW') " +
                        "AND start_time < ? " +
                        "AND end_time > ?";

        try (
                Connection c = connection();
                PreparedStatement p = c.prepareStatement(q)
        ) {

            p.setInt(1, dentistId);
            p.setDate(2, Date.valueOf(date));
            p.setTime(3, Time.valueOf(end));
            p.setTime(4, Time.valueOf(start));

            try (ResultSet r = p.executeQuery()) {

                r.next();

                return r.getInt(1) > 0;
            }
        }
    }

    @Override
    public boolean existsConflictExcept(int dentistId, LocalDate date, LocalTime start, LocalTime end, int appointmentId) throws Exception {
        String q="SELECT COUNT(*) FROM appointments WHERE dentist_id=? AND appointment_date=? AND appointment_id<>? AND status NOT IN ('CANCELLED','NO_SHOW') AND start_time < ? AND end_time > ?";
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(q)) {
            p.setInt(1,dentistId); p.setDate(2,Date.valueOf(date)); p.setInt(3,appointmentId);
            p.setTime(4,Time.valueOf(end)); p.setTime(5,Time.valueOf(start));
            try(ResultSet r=p.executeQuery()){r.next();return r.getInt(1)>0;}
        }
    }

    @Override
    public String generateNextCode() throws Exception {

        try (
                Connection c = connection();
                Statement s = c.createStatement();
                ResultSet r = s.executeQuery(
                        "SELECT COALESCE(MAX(appointment_id), 0) + 1 " +
                                "FROM appointments"
                )
        ) {

            r.next();

            return String.format(
                    "APT-%06d",
                    r.getInt(1)
            );
        }
    }

    @Override
    public boolean save(Appointment a) throws Exception {

        String q =
                "INSERT INTO appointments " +
                        "(appointment_no, patient_id, dentist_id, treatment_id, " +
                        "appointment_date, start_time, end_time, status, remarks, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection c = connection()) {

            try {

                c.setTransactionIsolation(
                        Connection.TRANSACTION_SERIALIZABLE
                );

                c.setAutoCommit(false);

                String conflict =
                        "SELECT COUNT(*) " +
                                "FROM appointments " +
                                "WHERE dentist_id = ? " +
                                "AND appointment_date = ? " +
                                "AND status NOT IN ('CANCELLED', 'NO_SHOW') " +
                                "AND start_time < ? " +
                                "AND end_time > ?";

                try (
                        PreparedStatement cp =
                                c.prepareStatement(conflict)
                ) {

                    cp.setInt(
                            1,
                            a.getDentist().getDentistId()
                    );

                    cp.setDate(
                            2,
                            Date.valueOf(
                                    a.getAppointmentDate()
                            )
                    );

                    cp.setTime(
                            3,
                            Time.valueOf(
                                    a.getEndTime()
                            )
                    );

                    cp.setTime(
                            4,
                            Time.valueOf(
                                    a.getStartTime()
                            )
                    );

                    try (ResultSet r = cp.executeQuery()) {

                        r.next();

                        if (r.getInt(1) > 0) {

                            c.rollback();

                            return false;
                        }
                    }
                }

                try (
                        PreparedStatement p =
                                c.prepareStatement(q)
                ) {

                    p.setString(
                            1,
                            a.getAppointmentNo()
                    );

                    p.setInt(
                            2,
                            a.getPatient().getPatientId()
                    );

                    p.setInt(
                            3,
                            a.getDentist().getDentistId()
                    );

                    p.setInt(
                            4,
                            a.getTreatment().getTreatmentId()
                    );

                    p.setDate(
                            5,
                            Date.valueOf(
                                    a.getAppointmentDate()
                            )
                    );

                    p.setTime(
                            6,
                            Time.valueOf(
                                    a.getStartTime()
                            )
                    );

                    p.setTime(
                            7,
                            Time.valueOf(
                                    a.getEndTime()
                            )
                    );

                    p.setString(
                            8,
                            a.getStatus().name()
                    );

                    p.setString(
                            9,
                            a.getRemarks()
                    );

                    boolean ok = p.executeUpdate() == 1;

                    c.commit();

                    return ok;
                }

            } catch (Exception e) {

                c.rollback();

                throw e;

            } finally {

                c.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean update(Appointment a) throws Exception {
        String q="UPDATE appointments SET patient_id=?,dentist_id=?,treatment_id=?,appointment_date=?,start_time=?,end_time=?,status=?,remarks=? WHERE appointment_id=?";
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(q)) {
            p.setInt(1,a.getPatient().getPatientId()); p.setInt(2,a.getDentist().getDentistId()); p.setInt(3,a.getTreatment().getTreatmentId());
            p.setDate(4,Date.valueOf(a.getAppointmentDate())); p.setTime(5,Time.valueOf(a.getStartTime())); p.setTime(6,Time.valueOf(a.getEndTime()));
            p.setString(7,a.getStatus().name()); p.setString(8,a.getRemarks()); p.setInt(9,a.getAppointmentId());
            return p.executeUpdate()==1;
        }
    }

    @Override
    public List<Appointment> findByDateRange(
            LocalDate from,
            LocalDate to,
            Integer dentistId
    ) throws Exception {

        String q =
                base() +
                        "WHERE a.appointment_date BETWEEN ? AND ?" +
                        (dentistId == null
                                ? ""
                                : " AND a.dentist_id = ?") +
                        " ORDER BY a.appointment_date, a.start_time";

        List<Appointment> list = new ArrayList<>();

        try (
                Connection c = connection();
                PreparedStatement p = c.prepareStatement(q)
        ) {

            p.setDate(
                    1,
                    Date.valueOf(from)
            );

            p.setDate(
                    2,
                    Date.valueOf(to)
            );

            if (dentistId != null) {

                p.setInt(
                        3,
                        dentistId
                );
            }

            try (ResultSet r = p.executeQuery()) {

                while (r.next()) {

                    list.add(
                            map(r)
                    );
                }
            }
        }

        return list;
    }

    @Override
    public Optional<Appointment> findById(int id) throws Exception {

        return findOne(
                base() +
                        "WHERE a.appointment_id = ?",
                id
        );
    }

    @Override
    public Optional<Appointment> findByAppointmentNo(
            String no
    ) throws Exception {

        return findOne(
                base() +
                        "WHERE a.appointment_no = ?",
                no
        );
    }

    private Optional<Appointment> findOne(
            String q,
            Object value
    ) throws Exception {

        try (
                Connection c = connection();
                PreparedStatement p = c.prepareStatement(q)
        ) {

            p.setObject(
                    1,
                    value
            );

            try (ResultSet r = p.executeQuery()) {

                return r.next()
                        ? Optional.of(map(r))
                        : Optional.empty();
            }
        }
    }

    @Override
    public boolean updateStatus(
            int id,
            AppointmentStatus status
    ) throws Exception {

        String q =
                "UPDATE appointments " +
                        "SET status = ? " +
                        "WHERE appointment_id = ?";

        try (
                Connection c = connection();
                PreparedStatement p = c.prepareStatement(q)
        ) {

            p.setString(
                    1,
                    status.name()
            );

            p.setInt(
                    2,
                    id
            );

            return p.executeUpdate() == 1;
        }
    }
}
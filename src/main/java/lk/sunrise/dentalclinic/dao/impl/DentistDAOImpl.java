package lk.sunrise.dentalclinic.dao.impl;

import lk.sunrise.dentalclinic.dao.DentistDAO;
import lk.sunrise.dentalclinic.entity.Dentist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DentistDAOImpl extends BaseDAOImpl implements DentistDAO {

    private Dentist map(ResultSet r) throws SQLException {
        Dentist d = new Dentist();
        d.setDentistId(r.getInt("dentist_id"));
        d.setDentistCode(r.getString("dentist_code"));
        d.setFullName(r.getString("full_name"));
        d.setSlmcNumber(r.getString("slmc_number"));
        d.setSpecialization(r.getString("specialization"));
        d.setContactNumber(r.getString("contact_number"));
        d.setEmail(r.getString("email"));
        d.setConsultationFee(r.getBigDecimal("consultation_fee"));
        d.setWorkingHoursStart(time(r, "working_hours_start"));
        d.setWorkingHoursEnd(time(r, "working_hours_end"));
        d.setAvailable(r.getBoolean("available"));
        return d;
    }

    @Override
    public Optional<Dentist> findById(int id) throws Exception {
        String sql = "SELECT * FROM dentists WHERE dentist_id = ?";
        try (Connection c = connection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? Optional.of(map(r)) : Optional.empty();
            }
        }
    }

    @Override
    public Optional<Dentist> findByCode(String code) throws Exception {
        String sql = "SELECT * FROM dentists WHERE dentist_code = ?";
        try (Connection c = connection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, code);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? Optional.of(map(r)) : Optional.empty();
            }
        }
    }

    @Override
    public Optional<Dentist> findBySlmcNumber(String slmcNumber) throws Exception {
        String sql = "SELECT * FROM dentists WHERE slmc_number = ?";
        try (Connection c = connection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, slmcNumber);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? Optional.of(map(r)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Dentist> findAll() throws Exception {
        return searchInternal("SELECT * FROM dentists ORDER BY full_name", null);
    }

    @Override
    public List<Dentist> findAvailable() throws Exception {
        return searchInternal("SELECT * FROM dentists WHERE available = TRUE ORDER BY full_name", null);
    }

    @Override
    public List<Dentist> search(String keyword) throws Exception {
        String sql = "SELECT * FROM dentists " +
                "WHERE dentist_code LIKE ? OR full_name LIKE ? OR slmc_number LIKE ? " +
                "OR specialization LIKE ? OR contact_number LIKE ? " +
                "ORDER BY full_name";
        String value = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        return searchInternal(sql, value);
    }

    private List<Dentist> searchInternal(String sql, String value) throws Exception {
        List<Dentist> dentists = new ArrayList<>();
        try (Connection c = connection(); PreparedStatement p = c.prepareStatement(sql)) {
            if (value != null) {
                for (int i = 1; i <= 5; i++) {
                    p.setString(i, value);
                }
            }
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    dentists.add(map(r));
                }
            }
        }
        return dentists;
    }

    @Override
    public String generateNextCode() throws Exception {
        String sql = "SELECT COALESCE(MAX(dentist_id), 0) + 1 FROM dentists";
        try (Connection c = connection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            r.next();
            return String.format("DEN-%04d", r.getInt(1));
        }
    }

    @Override
    public boolean save(Dentist dentist) throws Exception {
        String sql = "INSERT INTO dentists " +
                "(dentist_code, full_name, slmc_number, specialization, contact_number, email, " +
                "consultation_fee, working_hours_start, working_hours_end, available) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = connection(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setValues(p, dentist);
            int affected = p.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) dentist.setDentistId(keys.getInt(1));
            }
            return true;
        }
    }

    @Override
    public boolean update(Dentist dentist) throws Exception {
        String sql = "UPDATE dentists SET full_name = ?, slmc_number = ?, specialization = ?, " +
                "contact_number = ?, email = ?, consultation_fee = ?, working_hours_start = ?, " +
                "working_hours_end = ?, available = ? WHERE dentist_id = ?";

        try (Connection c = connection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, dentist.getFullName());
            p.setString(2, dentist.getSlmcNumber());
            p.setString(3, dentist.getSpecialization());
            p.setString(4, dentist.getContactNumber());
            p.setString(5, dentist.getEmail());
            p.setBigDecimal(6, dentist.getConsultationFee());
            setTime(p, 7, dentist.getWorkingHoursStart());
            setTime(p, 8, dentist.getWorkingHoursEnd());
            p.setBoolean(9, dentist.isAvailable());
            p.setInt(10, dentist.getDentistId());
            return p.executeUpdate() > 0;
        }
    }

    private void setValues(PreparedStatement p, Dentist d) throws SQLException {
        p.setString(1, d.getDentistCode());
        p.setString(2, d.getFullName());
        p.setString(3, d.getSlmcNumber());
        p.setString(4, d.getSpecialization());
        p.setString(5, d.getContactNumber());
        p.setString(6, d.getEmail());
        p.setBigDecimal(7, d.getConsultationFee());
        setTime(p, 8, d.getWorkingHoursStart());
        setTime(p, 9, d.getWorkingHoursEnd());
        p.setBoolean(10, d.isAvailable());
    }
}

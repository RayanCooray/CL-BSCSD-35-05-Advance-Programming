package lk.sunrise.dentalclinic.dao.impl;

import lk.sunrise.dentalclinic.dao.TreatmentDAO;
import lk.sunrise.dentalclinic.entity.Treatment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreatmentDAOImpl extends BaseDAOImpl implements TreatmentDAO {

    private Treatment map(ResultSet r) throws Exception {
        Treatment t = new Treatment();
        t.setTreatmentId(r.getInt("treatment_id"));
        t.setTreatmentCode(r.getString("treatment_code"));
        t.setName(r.getString("name"));
        t.setDescription(r.getString("description"));
        t.setCategory(r.getString("category"));
        t.setBasePrice(r.getBigDecimal("base_price"));
        t.setDurationMinutes(r.getInt("duration_minutes"));
        t.setActive(r.getBoolean("active"));
        return t;
    }

    private List<Treatment> list(String sql) throws Exception {
        List<Treatment> treatments = new ArrayList<>();

        try (Connection c = connection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {

            while (r.next()) {
                treatments.add(map(r));
            }
        }

        return treatments;
    }

    @Override
    public Optional<Treatment> findById(int id) throws Exception {
        String sql = "SELECT * FROM treatments WHERE treatment_id=?";

        try (Connection c = connection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, id);

            try (ResultSet r = p.executeQuery()) {
                return r.next() ? Optional.of(map(r)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Treatment> findAll() throws Exception {
        return list("SELECT * FROM treatments ORDER BY name");
    }

    @Override
    public List<Treatment> findActive() throws Exception {
        return list("SELECT * FROM treatments WHERE active=TRUE ORDER BY name");
    }

    @Override
    public boolean existsByName(String name, int excludeId) throws Exception {
        String sql = "SELECT COUNT(*) FROM treatments WHERE LOWER(name)=LOWER(?) AND treatment_id<>?";

        try (Connection c = connection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, name.trim());
            p.setInt(2, excludeId);

            try (ResultSet r = p.executeQuery()) {
                r.next();
                return r.getInt(1) > 0;
            }
        }
    }

    @Override
    public String generateNextCode() throws Exception {
        String sql = "SELECT COALESCE(MAX(treatment_id),0)+1 FROM treatments";

        try (Connection c = connection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {

            r.next();
            return String.format("TRT-%06d", r.getInt(1));
        }
    }

    @Override
    public boolean save(Treatment treatment) throws Exception {
        String sql = "INSERT INTO treatments " +
                "(treatment_code,name,description,category,base_price,duration_minutes,active) " +
                "VALUES (?,?,?,?,?,?,?)";

        try (Connection c = connection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, treatment.getTreatmentCode());
            p.setString(2, treatment.getName());
            p.setString(3, treatment.getDescription());
            p.setString(4, treatment.getCategory());
            p.setBigDecimal(5, treatment.getBasePrice());
            p.setInt(6, treatment.getDurationMinutes());
            p.setBoolean(7, treatment.isActive());

            return p.executeUpdate() == 1;
        }
    }

    @Override
    public boolean update(Treatment treatment) throws Exception {
        String sql = "UPDATE treatments SET name=?,description=?,category=?,base_price=?," +
                "duration_minutes=?,active=? WHERE treatment_id=?";

        try (Connection c = connection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, treatment.getName());
            p.setString(2, treatment.getDescription());
            p.setString(3, treatment.getCategory());
            p.setBigDecimal(4, treatment.getBasePrice());
            p.setInt(5, treatment.getDurationMinutes());
            p.setBoolean(6, treatment.isActive());
            p.setInt(7, treatment.getTreatmentId());

            return p.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM treatments WHERE treatment_id=?";

        try (Connection c = connection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, id);

            return p.executeUpdate() == 1;
        }
    }
}

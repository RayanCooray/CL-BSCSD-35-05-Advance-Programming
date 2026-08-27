package lk.sunrise.dentalclinic.dao.impl;

import java.sql.*;
import java.util.*;
import lk.sunrise.dentalclinic.dao.PatientDAO;
import lk.sunrise.dentalclinic.entity.*;

public class PatientDAOImpl extends BaseDAOImpl implements PatientDAO {
    private Patient map(ResultSet r)throws Exception {
        Patient p=new Patient();
        p.setPatientId(r.getInt("patient_id"));
        p.setPatientCode(r.getString("patient_code"));
        p.setFullName(r.getString("full_name"));
        p.setDateOfBirth(date(r,"date_of_birth"));
        p.setGender(Gender.valueOf(r.getString("gender")));
        p.setContactNumber(r.getString("contact_number"));
        p.setEmail(r.getString("email"));
        p.setAddress(r.getString("address"));
        p.setMedicalHistory(r.getString("medical_history"));
        p.setRegisteredAt(timestamp(r,"registered_at"));
        return p;
    }
    private Optional<Patient> one(String sql,Object v)throws Exception {
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)) {
            p.setObject(1,v);
            try(ResultSet r=p.executeQuery()) {
                return r.next()?Optional.of(map(r)):Optional.empty();
            }
        }
    }
    public Optional<Patient> findById(int id)throws Exception {
        return one("SELECT * FROM patients WHERE patient_id=?",id);
    }
    public Optional<Patient> findByCode(String x)throws Exception {
        return one("SELECT * FROM patients WHERE patient_code=?",x);
    }
    public List<Patient> search(String k)throws Exception {
        List<Patient>a=new ArrayList<>();
        String q="SELECT * FROM patients WHERE patient_code LIKE ? OR full_name LIKE ? OR contact_number LIKE ? ORDER BY full_name";
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(q)) {
            String x="%"+(k==null?"":k)+"%";
            for(int i=1;i<=3;i++)p.setString(i,x);
            try(ResultSet r=p.executeQuery()) {
                while(r.next())a.add(map(r));
            }
        }
        return a;
    }
    public boolean existsByContact(String x)throws Exception {
        try(Connection c=connection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM patients WHERE contact_number=?")) {
            p.setString(1,x);
            try(ResultSet r=p.executeQuery()) {
                r.next();
                return r.getInt(1)>0;
            }
        }
    }

    @Override
    public boolean existsByContactExcept(String x, int patientId)throws Exception {
        try(Connection c=connection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM patients WHERE contact_number=? AND patient_id<>?")) {
            p.setString(1,x);
            p.setInt(2,patientId);
            try(ResultSet r=p.executeQuery()) {
                r.next();
                return r.getInt(1)>0;
            }
        }
    }
    public String generateNextCode()throws Exception {
        try(Connection c=connection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COALESCE(MAX(patient_id),0)+1 FROM patients")) {
            r.next();
            return String.format("PAT-%06d",r.getInt(1));
        }
    }
    @Override
    public boolean save(Patient x)throws Exception {
        String q="INSERT INTO patients(patient_code,full_name,date_of_birth,gender,contact_number,email,address,medical_history,registered_at) VALUES(?,?,?,?,?,?,?,?,NOW())";
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(q,Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1,x.getPatientCode());
            p.setString(2,x.getFullName());
            setDate(p,3,x.getDateOfBirth());
            p.setString(4,x.getGender().name());
            p.setString(5,x.getContactNumber());
            p.setString(6,x.getEmail());
            p.setString(7,x.getAddress());
            p.setString(8,x.getMedicalHistory());
            if(p.executeUpdate()!=1)return false;
            try(ResultSet r=p.getGeneratedKeys()) {
                if(r.next())x.setPatientId(r.getInt(1));
            }
            return true;
        }
    }

    @Override
    public boolean update(Patient x)throws Exception {
        String q="UPDATE patients SET full_name=?,date_of_birth=?,gender=?,contact_number=?,email=?,address=?,medical_history=? WHERE patient_id=?";
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(q)) {
            p.setString(1,x.getFullName());
            setDate(p,2,x.getDateOfBirth());
            p.setString(3,x.getGender().name());
            p.setString(4,x.getContactNumber());
            p.setString(5,x.getEmail());
            p.setString(6,x.getAddress());
            p.setString(7,x.getMedicalHistory());
            p.setInt(8,x.getPatientId());
            return p.executeUpdate()==1;
        }
    }
}

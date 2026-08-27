package lk.sunrise.dentalclinic.dao.impl;

import java.sql.*;
import java.util.*;
import lk.sunrise.dentalclinic.dao.InvoiceDAO;
import lk.sunrise.dentalclinic.entity.*;

public class InvoiceDAOImpl extends BaseDAOImpl implements InvoiceDAO {
    private Invoice map(ResultSet r)throws Exception {
        Invoice i=new Invoice();
        i.setInvoiceId(r.getInt("invoice_id"));
        i.setInvoiceNo(r.getString("invoice_no"));
        Patient patient=new Patient();
        patient.setPatientId(r.getInt("patient_id"));
        i.setPatient(patient);
        Appointment appointment=new Appointment();
        appointment.setAppointmentId(r.getInt("appointment_id"));
        i.setAppointment(appointment);
        i.setIssueDate(timestamp(r,"issue_date"));
        i.setSubTotal(r.getBigDecimal("sub_total"));
        i.setConsultationFee(r.getBigDecimal("consultation_fee"));
        i.setTaxRate(r.getBigDecimal("tax_rate"));
        i.setTaxAmount(r.getBigDecimal("tax_amount"));
        i.setDiscount(r.getBigDecimal("discount"));
        i.setTotalAmount(r.getBigDecimal("total_amount"));
        i.setStatus(PaymentStatus.valueOf(r.getString("status")));
        return i;
    }
    public Optional<Invoice> findById(int id)throws Exception {
        try(Connection c=connection();PreparedStatement p=c.prepareStatement("SELECT * FROM invoice WHERE invoice_id=?")) {
            p.setInt(1,id);
            try(ResultSet r=p.executeQuery()) {
                if(!r.next())return Optional.empty();
                Invoice i=map(r);
                i.setItems(items(c,id));
                return Optional.of(i);
            }
        }
    }
    public Optional<Invoice> findByAppointmentId(int id)throws Exception {
        try(Connection c=connection();PreparedStatement p=c.prepareStatement("SELECT * FROM invoice WHERE appointment_id=?")) {
            p.setInt(1,id);
            try(ResultSet r=p.executeQuery()) {
                if(!r.next())return Optional.empty();
                Invoice i=map(r);
                i.setItems(items(c,i.getInvoiceId()));
                return Optional.of(i);
            }
        }
    }
    private List<InvoiceItem> items(Connection c,int id)throws Exception {
        List<InvoiceItem>a=new ArrayList<>();
        try(PreparedStatement p=c.prepareStatement("SELECT * FROM invoice_item WHERE invoice_id=? ORDER BY item_id")) {
            p.setInt(1,id);
            try(ResultSet r=p.executeQuery()) {
                while(r.next()) {
                    InvoiceItem x=new InvoiceItem();
                    x.setItemId(r.getInt("item_id"));
                    x.setDescription(r.getString("description"));
                    x.setQuantity(r.getInt("quantity"));
                    x.setUnitPrice(r.getBigDecimal("unit_price"));
                    x.setLineTotal(r.getBigDecimal("line_total"));
                    a.add(x);
                }
            }
        }
        return a;
    }
    public String generateNextCode()throws Exception {
        try(Connection c=connection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COALESCE(MAX(invoice_id),0)+1 FROM invoice")) {
            r.next();
            return String.format("INV-%06d",r.getInt(1));
        }
    }
    public boolean save(Invoice i)throws Exception {
        try(Connection c=connection()) {
            try {
                c.setAutoCommit(false);
                saveWithItems(c,i);
                c.commit();
                return true;
            }
            catch(Exception e) {
                c.rollback();
                throw e;
            }
            finally {
                c.setAutoCommit(true);
            }
        }
    }
    public void saveWithItems(Connection c,Invoice i)throws Exception {
        String q="INSERT INTO invoice(invoice_no,patient_id,appointment_id,issue_date,sub_total,consultation_fee,tax_rate,tax_amount,discount,total_amount,status) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement p=c.prepareStatement(q,Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1,i.getInvoiceNo());
            p.setInt(2,i.getPatient().getPatientId());
            p.setInt(3,i.getAppointment().getAppointmentId());
            setTimestamp(p,4,i.getIssueDate());
            p.setBigDecimal(5,i.getSubTotal());
            p.setBigDecimal(6,i.getConsultationFee());
            p.setBigDecimal(7,i.getTaxRate());
            p.setBigDecimal(8,i.getTaxAmount());
            p.setBigDecimal(9,i.getDiscount());
            p.setBigDecimal(10,i.getTotalAmount());
            p.setString(11,i.getStatus().name());
            p.executeUpdate();
            try(ResultSet k=p.getGeneratedKeys()) {
                if(k.next())i.setInvoiceId(k.getInt(1));
            }
        }
        String iq="INSERT INTO invoice_item(invoice_id,description,quantity,unit_price,line_total) VALUES(?,?,?,?,?)";
        try(PreparedStatement p=c.prepareStatement(iq)) {
            for(InvoiceItem x:i.getItems()) {
                p.setInt(1,i.getInvoiceId());
                p.setString(2,x.getDescription());
                p.setInt(3,x.getQuantity());
                p.setBigDecimal(4,x.getUnitPrice());
                p.setBigDecimal(5,x.getLineTotal());
                p.addBatch();
            }
            p.executeBatch();
        }
    }
}

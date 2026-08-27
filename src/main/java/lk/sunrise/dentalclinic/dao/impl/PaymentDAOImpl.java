package lk.sunrise.dentalclinic.dao.impl;

import java.sql.*;
import java.util.*;
import java.math.*;
import lk.sunrise.dentalclinic.dao.PaymentDAO;
import lk.sunrise.dentalclinic.entity.*;

public class PaymentDAOImpl extends BaseDAOImpl implements PaymentDAO {
    public boolean save(Payment x)throws Exception {
        try(Connection c=connection()) {
            try {
                c.setAutoCommit(false);
                String q="INSERT INTO payment(invoice_id,amount_paid,payment_date,method) VALUES(?,?,?,?)";
                try(PreparedStatement p=c.prepareStatement(q)) {
                    p.setInt(1,x.getInvoice().getInvoiceId());
                    p.setBigDecimal(2,x.getAmountPaid());
                    setTimestamp(p,3,x.getPaymentDate());
                    p.setString(4,x.getMethod().name());
                    p.executeUpdate();
                }
                BigDecimal total=invoiceTotal(c,x.getInvoice().getInvoiceId());
                BigDecimal paid=getTotalPaid(c,x.getInvoice().getInvoiceId());
                PaymentStatus s=paid.compareTo(total)>=0?PaymentStatus.PAID:PaymentStatus.PARTIALLY_PAID;
                try(PreparedStatement p=c.prepareStatement("UPDATE invoice SET status=? WHERE invoice_id=?")) {
                    p.setString(1,s.name());
                    p.setInt(2,x.getInvoice().getInvoiceId());
                    p.executeUpdate();
                }
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
    private BigDecimal invoiceTotal(Connection c,int id)throws Exception {
        try(PreparedStatement p=c.prepareStatement("SELECT total_amount FROM invoice WHERE invoice_id=?")) {
            p.setInt(1,id);
            try(ResultSet r=p.executeQuery()) {
                r.next();
                return r.getBigDecimal(1);
            }
        }
    }
    private BigDecimal getTotalPaid(Connection c,int id)throws Exception {
        try(PreparedStatement p=c.prepareStatement("SELECT COALESCE(SUM(amount_paid),0) FROM payment WHERE invoice_id=?")) {
            p.setInt(1,id);
            try(ResultSet r=p.executeQuery()) {
                r.next();
                return r.getBigDecimal(1);
            }
        }
    }
    public BigDecimal getTotalPaid(int id)throws Exception {
        try(Connection c=connection()) {
            return getTotalPaid(c,id);
        }
    }
    public List<Payment> findByInvoiceId(int id)throws Exception {
        List<Payment>a=new ArrayList<>();
        String q="SELECT * FROM payment WHERE invoice_id=? ORDER BY payment_date";
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(q)) {
            p.setInt(1,id);
            try(ResultSet r=p.executeQuery()) {
                while(r.next()) {
                    Payment x=new Payment();
                    x.setPaymentId(r.getInt("payment_id"));
                    x.setAmountPaid(r.getBigDecimal("amount_paid"));
                    x.setPaymentDate(timestamp(r,"payment_date"));
                    x.setMethod(PaymentMethod.valueOf(r.getString("method")));
                    a.add(x);
                }
            }
        }
        return a;
    }
}

package lk.sunrise.dentalclinic.dao.impl;

import java.sql.*;
import java.time.*;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.util.DatabaseConnection;

public abstract class BaseDAOImpl {
    protected Connection connection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
    protected void setDate(PreparedStatement p,int i,LocalDate v)throws SQLException {
        if(v==null)p.setNull(i,Types.DATE);
        else p.setDate(i,Date.valueOf(v));
    }
    protected void setTime(PreparedStatement p,int i,LocalTime v)throws SQLException {
        if(v==null)p.setNull(i,Types.TIME);
        else p.setTime(i,Time.valueOf(v));
    }
    protected void setTimestamp(PreparedStatement p,int i,LocalDateTime v)throws SQLException {
        if(v==null)p.setNull(i,Types.TIMESTAMP);
        else p.setTimestamp(i,Timestamp.valueOf(v));
    }
    protected LocalDate date(ResultSet r,String c)throws SQLException {
        Date v=r.getDate(c);
        return v==null?null:v.toLocalDate();
    }
    protected LocalTime time(ResultSet r,String c)throws SQLException {
        Time v=r.getTime(c);
        return v==null?null:v.toLocalTime();
    }
    protected LocalDateTime timestamp(ResultSet r,String c)throws SQLException {
        Timestamp v=r.getTimestamp(c);
        return v==null?null:v.toLocalDateTime();
    }
}

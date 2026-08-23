package lk.sunrise.dentalclinic.dao.impl;

import java.sql.*;
import java.util.*;
import lk.sunrise.dentalclinic.dao.UserDAO;
import lk.sunrise.dentalclinic.entity.*;

public class UserDAOImpl extends BaseDAOImpl implements UserDAO {
    private User map(ResultSet r)throws Exception {
        User u=new User();
        u.setUserId(r.getInt("user_id"));
        u.setUsername(r.getString("username"));
        u.setPasswordHash(r.getString("password_hash"));
        u.setFullName(r.getString("full_name"));
        u.setEmail(r.getString("email"));
        u.setRole(UserRole.valueOf(r.getString("role")));
        u.setActive(r.getBoolean("active"));
        return u;
    }
    public Optional<User> findByUsername(String x)throws Exception {
        try(Connection c=connection();PreparedStatement p=c.prepareStatement("SELECT * FROM users WHERE username=?")) {
            p.setString(1,x);
            try(ResultSet r=p.executeQuery()) {
                return r.next()?Optional.of(map(r)):Optional.empty();
            }
        }
    }
    public boolean existsByUsername(String x)throws Exception {
        try(Connection c=connection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM users WHERE username=?")) {
            p.setString(1,x);
            try(ResultSet r=p.executeQuery()) {
                r.next();
                return r.getInt(1)>0;
            }
        }
    }
    public boolean save(User u)throws Exception {
        String q="INSERT INTO users(username,password_hash,full_name,email,role,active) VALUES(?,?,?,?,?,?)";
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(q)) {
            p.setString(1,u.getUsername());
            p.setString(2,u.getPasswordHash());
            p.setString(3,u.getFullName());
            p.setString(4,u.getEmail());
            p.setString(5,u.getRole().name());
            p.setBoolean(6,u.isActive());
            return p.executeUpdate()==1;
        }
    }
}

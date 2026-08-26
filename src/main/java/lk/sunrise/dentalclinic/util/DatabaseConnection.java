package lk.sunrise.dentalclinic.util;

import java.sql.*;

public final class DatabaseConnection {
    private static DatabaseConnection instance;
    private static final String URL="jdbc:mysql://localhost:3306/sunrise_dental_clinic?useSSL=false&serverTimezone=UTC";
    private static final String USER="root";
    private static final String PASSWORD="1234";
    private DatabaseConnection() {
    }
    public static synchronized DatabaseConnection getInstance() {
        if(instance==null)instance=new DatabaseConnection();
        return instance;
    }
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
}

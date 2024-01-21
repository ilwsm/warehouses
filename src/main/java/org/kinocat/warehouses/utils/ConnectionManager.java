package org.kinocat.warehouses.utils;

import com.mysql.cj.log.Slf4JLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

final public class ConnectionManager {
    private static final String URL = "jdbc:mysql://localhost:3306/colossus?createDatabaseIfNotExist=true";

    private static final String USER = "root";
    private static final String PASS = "";
    private static final Properties PROP = new Properties();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        PROP.put("user", USER);
        PROP.put("password", PASS);
        PROP.put("logger", Slf4JLogger.class.getName());
        PROP.put("profileSQL", "true");
        PROP.put("profilerEventHandler", MyProfilerEventHandler.class.getName());
    }


    public static Connection open() {
        try {
            return DriverManager.getConnection(URL, PROP);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

package com.quizapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnectionUtil {

    private static final Properties DB_PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = DBConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("db.properties file is missing from classpath.");
            }
            DB_PROPERTIES.load(inputStream);
            Class.forName(DB_PROPERTIES.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException ex) {
            throw new ExceptionInInitializerError("Unable to initialize database configuration: " + ex.getMessage());
        }
    }

    private DBConnectionUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DB_PROPERTIES.getProperty("db.url"),
                DB_PROPERTIES.getProperty("db.username"),
                DB_PROPERTIES.getProperty("db.password"));
    }
}

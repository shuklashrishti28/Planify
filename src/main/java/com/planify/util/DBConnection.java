package com.planify.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Creates JDBC connections using deployment-provided environment settings. */
public class DBConnection {
    private static final String URL = System.getenv().getOrDefault("PLANIFY_DB_URL",
            "jdbc:mysql://localhost:3306/planify_db?useSSL=false&allowPublicKeyRetrieval=true");
    private static final String USER = System.getenv().getOrDefault("PLANIFY_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("PLANIFY_DB_PASSWORD", "Cosvin$1234");

    /** Opens one connection; callers must close it with try-with-resources. */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver missing!", e);
        }
    }
}

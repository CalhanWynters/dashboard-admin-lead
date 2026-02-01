package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Replace with your 2026 environment variables or config
    private static final String URL = "jdbc:mysql://localhost:3306/products_db";
    private static final String USER = "admin";
    private static final String PASS = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

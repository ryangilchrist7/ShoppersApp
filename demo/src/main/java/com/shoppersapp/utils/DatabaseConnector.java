package com.shoppersapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnector {
    @Deprecated
    public static Connection getConnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/bankdb?sslmode=disable";
            String user = "appadmin";
            String password = "sample";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("Connection not initialised");
            ;
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static Connection getTestConnection(String url, String user, String password) {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("Connection not initialised");
            ;
            e.printStackTrace();
            return null;
        }
    }
}

package edu.wgu.qam2schedulingapp.utility;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlDatabase {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//127.0.0.1/";
    private static final String dbName = "client_schedule";
    private static final String jbdcUrl = protocol + vendor + location + dbName;
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String username = "root";
    private static final String password = "12345";
    private static Connection connection = null;

    public static void connect() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(jbdcUrl, username, password);
            System.out.println("Connection established");
        } catch (ClassNotFoundException e) {
            System.out.println("Class ''" + driver + "'' not found:\n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL error:\n" + e.getMessage());
        }
    }

    public static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection has terminated successfully");
            } else {
                System.out.println("There is no connection");
            }
        } catch (SQLException e) {
            System.out.println("Sql error:" + e.getMessage());
        }
    }

    /**
     * @return connection
     */
    public static Connection getConnection() {
        return connection;
    }
}

package edu.wgu.qam2schedulingapp.helper;


import java.sql.*;

/**
 * @author Parham Modirniya
 */

public class SqlHelper {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//127.0.0.1/";
    private static final String dbName = "client_schedule";
    private static final String jbdcUrl = protocol + vendor + location + dbName;
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String username = "root";
    private static final String password = "12345";
    private static Connection connection = null;
    private static final String TAG = "SqlDatabase";

    public static void connect() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(jbdcUrl, username, password);
            Logs.info(TAG, "Connection established");
        } catch (ClassNotFoundException e) {
            Logs.error(TAG, "Class ''" + driver + "'' not found:\n" + e.getMessage());
        } catch (SQLException e) {
            Logs.error(TAG, "SQL exception:\t" + e.getMessage());
        }
    }

    public static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                Logs.info(TAG, "Connection has terminated successfully");
            } else {
                Logs.warning(TAG, "Attempted to close a connection that doesn't exist");
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Sql error:\t" + e.getMessage());
        }
    }

    public static ResultSet executeForResult(String strStatement) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(strStatement);
        } catch (SQLException e) {
            Logs.error(TAG, "SQL exception occurred while executing statement ->"
                            + strStatement + "\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * @return connection
     */
    public static Connection getConnection() {
        return connection;
    }
}

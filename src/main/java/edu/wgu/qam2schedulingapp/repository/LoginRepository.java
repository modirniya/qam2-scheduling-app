package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.model.User;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.SqlDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRepository {

    private static User currentUser;

    private static final String TAG = "LoginRepository";

    private static String statementPasswordRetrieval(String username) {
        return "SELECT User_ID, User_Name, Password FROM client_schedule.users WHERE User_Name = \"" + username + "\"";
    }

    public static boolean loginUser(User user) {
        try {
            ResultSet result = SqlDatabase.executeForResult(statementPasswordRetrieval(user.getUsername()));
            String password;
            while (result.next()) {
                password = result.getString("Password");
                if (password.equals(user.getPassword())) {
                    String username = result.getString("User_Name");
                    int id = result.getInt("User_ID");
                    currentUser = new User(id, username, password);
                }
            }
        } catch (SQLException e) {
            Logs.error(TAG, "SQL exception occurred while retrieving password");
        }
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}

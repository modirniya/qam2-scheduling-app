package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.model.User;
import edu.wgu.qam2schedulingapp.utility.LogsManager;
import edu.wgu.qam2schedulingapp.utility.SqlDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRepository {

    private static User currentUser;

    private static final String TAG = "LoginRepository";

    private static String statementPasswordRetrieval(String username) {
        return "SELECT Password FROM client_schedule.users WHERE User_Name = \"" + username + "\"";
    }

    public static boolean loginUser(User user) {
        try {
            ResultSet result = SqlDatabase.executeForResult(statementPasswordRetrieval(user.getUsername()));
            while (result.next())
                if (result.getString("Password").equals(user.getPassword())) {
                    currentUser = user;
                    return true;
                }
        } catch (SQLException e) {
            LogsManager.errorLog(TAG, "SQL exception occurred while retrieving password");
            return false;
        }
        return false;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}

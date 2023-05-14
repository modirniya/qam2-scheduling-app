package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.model.User;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.SqlDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    private static final String TAG = "LoginRepository";
    public final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private User currentUser;
    private static UserRepository instance;

    public static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    private UserRepository() {
        Logs.initLog(TAG);
        fetchAllUsers();
    }

    private void fetchAllUsers() {
        String statement = "SELECT User_ID, User_Name, Password FROM client_schedule.users";
        try {
            ResultSet resultSet = SqlDatabase.executeForResult(statement);
            while (resultSet.next()) {
                allUsers.add(User.fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Error fetching all users");
            throw new RuntimeException(e);
        }
    }

    public boolean loginUser(String username, String password) {
        for (User user : this.allUsers)
            if (user.getUsername().equals(username)
                && user.getPassword().equals(password))
                currentUser = user;
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User getUserById(int userId) {
        for (User user : allUsers)
            if (user.getId() == userId) return user;
        return null;
    }
}

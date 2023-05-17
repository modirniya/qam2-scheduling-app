package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.SqlHelper;
import edu.wgu.qam2schedulingapp.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is a singleton class that fetches and stores all user-related data from the database.
 * The class provides methods to retrieve users and to handle user authentication.
 * <p>
 * The class utilizes the {@link SqlHelper} class to execute SQL queries and
 * the {@link Logs} class to log events during its operations.
 * <p>
 * Fields:
 * <ul>
 *  <li>allUsers: An ObservableList that stores all fetched User objects.</li>
 *  <li>currentUser: A User object representing the currently logged-in user.</li>
 *  <li>TAG: A string identifier for logging purposes.</li>
 *  <li>instance: Static instance of UserRepository for singleton pattern.</li>
 * </ul>
 *
 * @author Parham Modirniya
 */
public class UserRepository {
    private static final String TAG = "LoginRepository";
    public final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private User currentUser;
    private static UserRepository instance;

    /**
     * Retrieves the singleton instance of UserRepository.
     * If the instance does not exist, it is created.
     *
     * @return The singleton instance of UserRepository.
     */
    public static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    /**
     * Private constructor for the UserRepository class.
     * Initiates log with the class tag and fetches all users from the database.
     */
    private UserRepository() {
        Logs.initLog(TAG);
        fetchAllUsers();
    }

    /**
     * Fetches all users from the database and adds them to the allUsers ObservableList.
     * If an SQL error occurs during this process, it logs an error and throws a RuntimeException.
     */
    private void fetchAllUsers() {
        String statement = "SELECT User_ID, User_Name, Password FROM client_schedule.users";
        try {
            ResultSet resultSet = SqlHelper.executeForResult(statement);
            while (resultSet.next()) {
                allUsers.add(User.fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Error fetching all users");
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the provided username and password match any User object in the allUsers ObservableList.
     * If a match is found, the matched User object is set as the currentUser.
     *
     * @param username The username to check.
     * @param password The password to check.
     * @return A boolean representing whether a user was successfully logged in.
     */
    public boolean loginUser(String username, String password) {
        for (User user : this.allUsers)
            if (user.getUsername().equals(username)
                && user.getPassword().equals(password))
                currentUser = user;
        return currentUser != null;
    }

    /**
     * Returns the User object representing the currently logged-in user.
     *
     * @return The currently logged-in User object.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Returns the User object with the provided user ID.
     * If no User is associated with the provided user ID, it returns null.
     *
     * @param userId The ID of the user to be retrieved.
     * @return The User object associated with the provided user ID.
     */
    public User getUserById(int userId) {
        for (User user : allUsers)
            if (user.getId() == userId) return user;
        return null;
    }
}
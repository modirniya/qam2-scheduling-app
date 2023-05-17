package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.SqlHelper;
import edu.wgu.qam2schedulingapp.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is a singleton class that fetches and stores all contacts from the database.
 * It also provides methods to retrieve contacts by ID.
 * <p>
 * The class utilizes the {@link SqlHelper} class to execute SQL queries and
 * the {@link Logs} class to log events during its operations.
 * <p>
 * Fields:
 * <ul>
 *  <li>allContacts: An ObservableList that stores all fetched Contact objects.</li>
 *  <li>TAG: A string identifier for logging purposes.</li>
 *  <li>instance: Static instance of ContactRepository for singleton pattern.</li>
 * </ul>
 *
 * @author Parham Modirniya
 */
public class ContactRepository {
    private static final String TAG = "ContactRepository";
    public final ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    private static ContactRepository instance;

    /**
     * Retrieves the singleton instance of ContactRepository.
     * If the instance does not exist, it is created.
     *
     * @return The singleton instance of ContactRepository.
     */
    public static ContactRepository getInstance() {
        if (instance == null) instance = new ContactRepository();
        return instance;
    }

    /**
     * Private constructor for the ContactRepository class.
     * Initiates log with the class tag and fetches all contacts.
     */
    private ContactRepository() {
        Logs.initLog(TAG);
        fetchAllContacts();
    }

    /**
     * Fetches all contacts from the database and stores them in the allContacts ObservableList.
     * If an SQL error occurs during this process, it logs an error and throws a RuntimeException.
     */
    private void fetchAllContacts() {
        String statement = "SELECT * FROM client_schedule.contacts";
        try {
            ResultSet resultSet = SqlHelper.executeForResult(statement);
            while (resultSet.next()) {
                allContacts.add(Contact.fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Error fetching all users");
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Contact object with the provided ID from the allContacts ObservableList.
     * If no Contact with the provided ID is found, it returns null.
     *
     * @param id The ID of the contact to be retrieved.
     * @return The Contact object with the provided ID, or null if no such Contact is found.
     */
    public Contact getContactById(int id) {
        for (Contact contact : allContacts) {
            if (contact.getId() == id)
                return contact;
        }
        return null;
    }
}

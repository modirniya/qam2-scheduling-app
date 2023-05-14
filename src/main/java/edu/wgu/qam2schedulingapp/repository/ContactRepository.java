package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.model.Contact;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.SqlHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactRepository {
    private static final String TAG = "ContactRepository";
    public final ObservableList<Contact> allContacts = FXCollections.observableArrayList();

    private static ContactRepository instance;

    public static ContactRepository getInstance() {
        if (instance == null) instance = new ContactRepository();
        return instance;
    }

    private ContactRepository() {
        Logs.initLog(TAG);
        fetchAllContacts();
    }

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

    public Contact getContactById(int id) {
        for (Contact contact : allContacts) {
            if (contact.getId() == id)
                return contact;
        }
        return null;
    }
}

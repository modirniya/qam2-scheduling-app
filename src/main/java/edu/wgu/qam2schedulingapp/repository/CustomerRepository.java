package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.SqlHelper;
import edu.wgu.qam2schedulingapp.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is a singleton class that fetches and stores all customers from the database.
 * It also provides methods to add, update, and delete customers, as well as retrieve a customer by ID.
 * <p>
 * The class utilizes the {@link SqlHelper} class to execute SQL queries and
 * the {@link Logs} class to log events during its operations.
 * <p>
 * Fields:
 * <ul>
 *  <li>allCustomers: An ObservableList that stores all fetched Customer objects.</li>
 *  <li>TAG: A string identifier for logging purposes.</li>
 *  <li>instance: Static instance of CustomerRepository for singleton pattern.</li>
 * </ul>
 *
 * @author Parham Modirniya
 */
public class CustomerRepository {
    private final String TAG = "CustomerRepository";
    public final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static CustomerRepository instance;

    /**
     * Retrieves the singleton instance of CustomerRepository.
     * If the instance does not exist, it is created.
     *
     * @return The singleton instance of CustomerRepository.
     */
    public static CustomerRepository getInstance() {
        if (instance == null) {
            instance = new CustomerRepository();
        }
        return instance;
    }

    /**
     * Private constructor for the CustomerRepository class.
     * Initiates log with the class tag and fetches all customers.
     */
    private CustomerRepository() {
        Logs.initLog(TAG);
        fetchAllCustomers();
    }

    /**
     * Fetches all customers from the database and stores them in the allCustomers ObservableList.
     * If an SQL error occurs during this process, it logs an error.
     * After the fetch operation, it logs the current number of customers in the list.
     */
    private void fetchAllCustomers() {
        allCustomers.clear();
        try {
            String statement = "SELECT * FROM client_schedule.customers";
            ResultSet result = SqlHelper.executeForResult(statement);
            while (result.next()) {
                allCustomers.add(Customer.fromResultSet(result));
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Fetching all customers has failed");
        }
        Logs.info(TAG, "Customers list has updated and currently has " + allCustomers.size() + " item(s)");
    }

    /**
     * Updates the information of a specific customer in the database using the provided Customer object.
     * If an SQL error occurs during this process, it logs an error.
     *
     * @param customer The Customer object that contains the updated information.
     */
    public void updateCustomer(Customer customer) {
        try {
            String strStatement = """
                    UPDATE client_schedule.customers
                        SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?,
                        Last_Update = UTC_TIMESTAMP(), Last_Updated_By = ?, Division_ID = ?
                        WHERE Customer_ID = ?""";
            PreparedStatement statement = SqlHelper.getConnection().prepareStatement(strStatement);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPostalCode());

            statement.setString(4, customer.getPhone());
            statement.setString(5, UserRepository.getInstance().getCurrentUser().getUsername());
            statement.setInt(6, customer.getDivisionId());
            statement.setInt(7, customer.getId());
            statement.executeUpdate();
            statement.close();
            fetchAllCustomers();
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while updating customer");
        }
    }

    /**
     * Adds a new customer to the database using the provided Customer object.
     * If an error occurs during this process, it logs an error.
     *
     * @param customer The Customer object that contains the information of the new customer.
     */
    public void addCustomer(Customer customer) {
        var username = UserRepository.getInstance().getCurrentUser().getUsername();
        PreparedStatement statement;
        try {
            String strStatement = """
                    INSERT INTO client_schedule.customers
                        (Customer_Name,Address,Postal_Code,Phone,Create_Date,
                        Created_By,Last_Update,Last_Updated_By,Division_ID)
                        VALUES (?,?,?,?,UTC_TIMESTAMP(),?,UTC_TIMESTAMP(),?,?)""";
            statement = SqlHelper.getConnection().prepareStatement(strStatement);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPostalCode());
            statement.setString(4, customer.getPhone());
            statement.setString(5, username);
            statement.setString(6, username);
            statement.setInt(7, customer.getDivisionId());
            statement.executeUpdate();
            statement.close();
            fetchAllCustomers();
        } catch (Exception e) {
            Logs.error(TAG, "Adding customer has failed");
        }
    }

    /**
     * Deletes a specific customer from the database using the provided Customer object.
     * If an SQL error occurs during this process, it logs an error.
     *
     * @param customer The Customer object that contains the information of the customer to be deleted.
     */
    public void deleteCustomer(Customer customer) {
        // TODO Make sure to delete appointment of this customer
        String strStatement = "DELETE FROM client_schedule.customers WHERE Customer_ID =" + customer.getId();
        try {
            SqlHelper.getConnection().createStatement().executeUpdate(strStatement);
            fetchAllCustomers();
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while deleting the customer");
        }
    }

    /**
     * Returns the Customer object with the provided ID from the allCustomers ObservableList.
     * If no Customer with the provided ID is found, it returns null.
     *
     * @param customerId The ID of the customer to be retrieved.
     * @return The Customer object with the provided ID, or null if no such Customer is found.
     */
    public Customer getCustomerById(int customerId) {
        for (Customer customer : allCustomers)
            if (customer.getId() == customerId)
                return customer;
        return null;
    }
}
package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.model.Customer;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.SqlDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerRepository {
    private final String TAG = "CustomerRepository";
    private final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static CustomerRepository instance;

    private CustomerRepository() {
        refreshAllCustomersData();
    }

    private void refreshAllCustomersData() {
        allCustomers.clear();
        try {
            String statement = "SELECT * FROM client_schedule.customers";
            ResultSet result = SqlDatabase.executeForResult(statement);
            while (result.next()) {
                allCustomers.add(Customer.fromResultSet(result));
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Fetching all customers has failed");
        }
    }

    public void addCustomer(Customer customer) {
        var user = LoginRepository.getCurrentUser();
        try {
            PreparedStatement statement = SqlDatabase.getConnection().prepareStatement(
                    """
                            INSERT INTO `client_schedule`.`customers`
                            (`Customer_Name`,`Address`,`Postal_Code`,`Phone`,`Create_Date`,`Created_By`,`Last_Update`,`Last_Updated_By`,`Division_ID`)\s
                            VALUES (?,?,?,?,UTC_TIMESTAMP(),?,UTC_TIMESTAMP(),?,?)""", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPostalCode());
            statement.setString(4, customer.getPhone());
            statement.setString(5, user.getUsername());
            statement.setString(6, user.getUsername());
            statement.setInt(7, customer.getDivisionId());
            statement.executeUpdate();
        } catch (Exception e) {
            Logs.error(TAG, "Adding customer has failed");
        } finally {
            refreshAllCustomersData();
        }
    }

    public static CustomerRepository getInstance() {
        if (instance == null) {
            instance = new CustomerRepository();
        }
        return instance;
    }

    public ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }

    public void updateCustomer(Customer customer) {

    }

    public boolean deleteCustomer(Customer customer) {
        return false;
    }
}


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

    public void updateCustomer(Customer customer) {
        var user = LoginRepository.getCurrentUser();
        try {
            String strStatement = """
                    UPDATE client_schedule.customers
                        SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, 
                        Last_Update = UTC_TIMESTAMP(), Last_Updated_By = ?, Division_ID = ?
                        WHERE Customer_ID = ?""";
            PreparedStatement statement = SqlDatabase.getConnection().prepareStatement(strStatement);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPostalCode());
            statement.setString(4, customer.getPhone());
            statement.setString(5, user.getUsername());
            statement.setInt(6, customer.getDivisionId());
            statement.setInt(7, customer.getId());
            statement.executeUpdate();
            statement.close();
            refreshAllCustomersData();
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while updating customer");
        }
    }

    public void addCustomer(Customer customer) {
        var user = LoginRepository.getCurrentUser();
        PreparedStatement statement;
        try {
            String strStatement = """
                    INSERT INTO client_schedule.customers
                        (Customer_Name,Address,Postal_Code,Phone,Create_Date,
                        Created_By,Last_Update,Last_Updated_By,Division_ID)
                        VALUES (?,?,?,?,UTC_TIMESTAMP(),?,UTC_TIMESTAMP(),?,?)""";
            statement = SqlDatabase.getConnection().prepareStatement(strStatement, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPostalCode());
            statement.setString(4, customer.getPhone());
            statement.setString(5, user.getUsername());
            statement.setString(6, user.getUsername());
            statement.setInt(7, customer.getDivisionId());
            statement.executeUpdate();
            statement.close();
            refreshAllCustomersData();
        } catch (Exception e) {
            Logs.error(TAG, "Adding customer has failed");
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

    public void deleteCustomer(Customer customer) {
        // TODO Make sure to delete appointment of this customer
        String strStatement = "Delete FROM client_schedule.customers WHERE Customer_ID =" + customer.getId();
        try {
            SqlDatabase.getConnection().createStatement().executeUpdate(strStatement);
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while deleting the customer");
        } finally {
            refreshAllCustomersData();
        }
    }
}

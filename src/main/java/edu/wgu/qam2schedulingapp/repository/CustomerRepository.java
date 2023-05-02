package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.ZonedDateTime;

public class CustomerRepository {
    private final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    private static CustomerRepository instance;

    private CustomerRepository() {
        addSampleCustomers();
    }

    private void addSampleCustomers() {
        Customer customer1 = new Customer(
                1,
                101,
                "John Doe",
                "123 Main St",
                "12345",
                "555-1234",
                "Creator",
                "Updater",
                ZonedDateTime.now(),
                ZonedDateTime.now());

        Customer customer2 = new Customer(
                2,
                102,
                "Jane Smith",
                "456 Elm St",
                "67890",
                "555-5678",
                "Creator",
                "Updater",
                ZonedDateTime.now(),
                ZonedDateTime.now());

        Customer customer3 = new Customer(
                3,
                103,
                "Alice Johnson",
                "789 Oak St",
                "13579",
                "555-2468",
                "Creator",
                "Updater",
                ZonedDateTime.now(),
                ZonedDateTime.now());

        allCustomers.addAll(customer1, customer2, customer3);

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

    public void addCustomer(Customer customer) {

    }

    public void updateCustomer(Customer customer) {

    }

    public boolean deleteCustomer(Customer customer) {
        return false;
    }
}


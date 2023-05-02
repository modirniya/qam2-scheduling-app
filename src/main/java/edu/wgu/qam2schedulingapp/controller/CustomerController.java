package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Customer;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.utility.LogsManager;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    private static final String TAG = "CustomerController";
    public TableView<Customer> tbAllCustomers;

    public void navigateBackToHome(ActionEvent actionEvent) {
    }

    public void addCustomer(ActionEvent actionEvent) {
    }

    public void modifyCustomer(ActionEvent actionEvent) {
    }

    public void deleteCustomer(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LogsManager.infoLog(TAG, "Initializing...");
        tbAllCustomers.setItems(CustomerRepository.getInstance().getAllCustomers());
    }
}

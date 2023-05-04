package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Customer;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.utility.Logs;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    private static final String TAG = "CustomerController";
    private static final String CUSTOMER_EDITOR_FXML = "/edu/wgu/qam2schedulingapp/view/customer-editor.fxml";
    private final Stage stageCustomerEditor = new Stage();
    public TableView<Customer> tbAllCustomers;
    private CustomerEditorController editorController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initControllerLog(TAG);
        tbAllCustomers.setItems(CustomerRepository.getInstance().getAllCustomers());
        FXMLLoader loaderCustomerEditor = new FXMLLoader(getClass().getResource(CUSTOMER_EDITOR_FXML));
        try {
            Parent parentCustomerEditor = loaderCustomerEditor.load();
            editorController = loaderCustomerEditor.getController();
            stageCustomerEditor.initModality(Modality.APPLICATION_MODAL);
            stageCustomerEditor.setScene(new Scene(parentCustomerEditor));
        } catch (IOException e) {
            Logs.error(TAG, "Loading customer editor failed");
        }
    }

    public void navigateBackToHome() {
        Stage stage = (Stage) tbAllCustomers.getScene().getWindow();
        stage.close();
    }

    public void addCustomer() {
        editorController.updateUI(CustomerEditorController.EditorMode.Add, null);
        stageCustomerEditor.showAndWait();
    }

    public void modifyCustomer() {
        Customer target = tbAllCustomers.getSelectionModel().getSelectedItem();
        if (target != null) {
            editorController.updateUI(CustomerEditorController.EditorMode.Modify, target);
            stageCustomerEditor.showAndWait();
        }
    }

    public void deleteCustomer() {
        Customer target = tbAllCustomers.getSelectionModel().getSelectedItem();
        if (target != null)
            CustomerRepository.getInstance().deleteCustomer(target);
    }
}

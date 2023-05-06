package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Customer;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.utility.Logs;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    private static final String TAG = "CustomerController";
    private static final String CUSTOMER_EDITOR_FXML = "/edu/wgu/qam2schedulingapp/view/customer-editor.fxml";
    //    private final Stage stageCustomerEditor = new Stage();
    public TableView<Customer> tbAllCustomers;
    public Label lbEvent;
    public TableColumn<Customer, Date> createdDate;
    public TableColumn<Customer, Date> lastUpdateDate;
    private final SimpleDateFormat tableDateFormat = new SimpleDateFormat("MM-dd-yy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initControllerLog(TAG);
        tbAllCustomers.setItems(CustomerRepository.getInstance().getAllCustomers());
        createdDate.setCellFactory(getColumnTableCellCallback());
        lastUpdateDate.setCellFactory(getColumnTableCellCallback());
    }

    public void navigateBackToHome() {
        Stage stage = (Stage) tbAllCustomers.getScene().getWindow();
        stage.close();
    }

    private void openCustomerEditor(Customer target) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(CUSTOMER_EDITOR_FXML));
        try {
            Parent parent = loader.load();
            CustomerEditorController controller = loader.getController();
            if (target != null)
                controller.updateUI(CustomerEditorController.EditorMode.Modify, target);
            else
                controller.updateUI(CustomerEditorController.EditorMode.Add, null);
            var stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            Logs.error(TAG, "Loading customer editor failed");
        }
    }

    public void addCustomer() {
        openCustomerEditor(null);
    }

    public void modifyCustomer() {
        Customer target = tbAllCustomers.getSelectionModel().getSelectedItem();
        if (target != null) {
            lbEvent.setText("");
            openCustomerEditor(target);
        } else
            lbEvent.setText("Unknown target: Select the customer in the table and press modify again.");
    }

    public void deleteCustomer() {
        Customer target = tbAllCustomers.getSelectionModel().getSelectedItem();
        if (target != null) {
            lbEvent.setText("");
            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Removing customer");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This customer's information and all associated appointments with this customer will be gone irreversibly.");
            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresentOrElse(
                    bt -> {
                        CustomerRepository.getInstance().deleteCustomer(target);
                        lbEvent.setText("Deletion successful: Customer has been removed from the database.");
                    }, () -> lbEvent.setText("Deletion aborted: No changes has been made to the database.")
            );
        } else {
            lbEvent.setText("Unknown target: Select the customer in the table and press delete again.");
        }
    }

    private Callback<TableColumn<Customer, Date>, TableCell<Customer, Date>> getColumnTableCellCallback() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : tableDateFormat.format(date));
            }
        };
    }
}

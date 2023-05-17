package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.model.Customer;
import edu.wgu.qam2schedulingapp.repository.AppointmentRepository;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.repository.LocationRepository;
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
import java.util.ResourceBundle;

/**
 * This is the controller class for the customer related operations in the scheduling app.
 * It handles user interactions with the customer's user interface.
 * It provides functionalities to add, modify, and delete customers.
 * It also navigates back to the home view and to the customer editor view.
 *
 * @author Parham Modirniya
 */
public class CustomerController implements Initializable {
    public TableView<Customer> tbCustomers;
    public TableColumn<Customer, Integer> tcSPR;
    public Label lbEvent;
    private static final String TAG = "CustomerController";
    private static final String CUSTOMER_EDITOR_FXML = "/edu/wgu/qam2schedulingapp/view/customer-editor.fxml";

    /**
     * Initializes the customer controller.
     * This method is called after all the FXMLLoader has completely loaded the FXML.
     * It initializes the log and sets the items for the customers table.
     *
     * @param url            A location used to resolve relative paths for the root object, or null.
     * @param resourceBundle The resources used to localize the root object, or null.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initLog(TAG);
        tbCustomers.setItems(CustomerRepository.getInstance().allCustomers);
        tcSPR.setCellFactory(getColumnTableCellCallback());
    }

    /**
     * Handles the action of adding a customer.
     * It navigates to the customer editor view with no customer as the target.
     */
    public void addCustomer() {
        navigateToEditor(null);
    }

    /**
     * Handles the action of modifying a customer.
     * It navigates to the customer editor view with the selected customer as the target.
     */
    public void modifyCustomer() {
        Customer target = tbCustomers.getSelectionModel().getSelectedItem();
        if (target != null) {
            lbEvent.setText("");
            navigateToEditor(target);
        } else
            lbEvent.setText("Unknown target: Select the customer in the table and press modify again.");
    }

    /**
     * Handles the action of deleting a customer.
     * It shows a confirmation dialog before deleting the selected customer and their associated appointments.
     */
    public void deleteCustomer() {
        Customer target = tbCustomers.getSelectionModel().getSelectedItem();
        if (target != null) {
            lbEvent.setText("");
            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Removing customer");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This customer's information and all associated appointments with this customer will be gone irreversibly.");
            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresentOrElse(
                    bt -> {
                        AppointmentRepository.getInstance().removeAppointmentsOfCustomer(target.getId());
                        CustomerRepository.getInstance().deleteCustomer(target);
                        lbEvent.setText("Deletion successful: Customer has been removed from the database.");
                    }, () -> lbEvent.setText("Deletion aborted: No changes has been made to the database.")
            );
        } else {
            lbEvent.setText("Unknown target: Select the customer in the table and press delete again.");
        }
    }

    /**
     * Navigates back to the home view.
     */
    public void navigateToHome() {
        Stage stage = (Stage) tbCustomers.getScene().getWindow();
        stage.close();
    }

    /**
     * Navigates to the customer editor view with the specified customer as the target.
     *
     * @param target The customer to be edited.
     */
    private void navigateToEditor(Customer target) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(CUSTOMER_EDITOR_FXML));
        try {
            Parent parent = loader.load();
            CustomerEditorController controller = loader.getController();
            controller.updateUI(target);
            var stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            Logs.error(TAG, "Loading customer editor failed");
        }
    }

    /**
     * Creates and returns a callback to be used for setting up a table cell.
     * This callback sets the text of the cell to the string representation of the SPR of the division with the specified ID.
     *
     * @return The created callback.
     */
    private Callback<TableColumn<Customer, Integer>,
            TableCell<Customer, Integer>> getColumnTableCellCallback() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer sprId, boolean isEmpty) {
                super.updateItem(sprId, isEmpty);
                setText(isEmpty ? null :
                        LocationRepository.getInstance().getSPRByDivisionId(sprId).toString());
            }
        };
    }

}

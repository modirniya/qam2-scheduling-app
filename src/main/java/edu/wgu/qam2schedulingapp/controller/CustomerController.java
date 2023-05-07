package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Customer;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.repository.LocationRepository;
import edu.wgu.qam2schedulingapp.utility.Logs;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    private static final String TAG = "CustomerController";
    private static final String CUSTOMER_EDITOR_FXML = "/edu/wgu/qam2schedulingapp/view/customer-editor.fxml";
    public TableView<Customer> tbCustomers;
    public Label lbEvent;
    public TableColumn<Customer, Integer> colSPR;
    private final SimpleDateFormat tableDateFormat = new SimpleDateFormat("MM-dd-yy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initLog(TAG);
        tbCustomers.setItems(CustomerRepository.getInstance().allCustomers);
        colSPR.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer sprId, boolean isDateEmpty) {
                super.updateItem(sprId, isDateEmpty);
                if (sprId != null) {
                    var spr = LocationRepository.getInstance().getSPRByDivisionId(sprId);
                    setText(spr.toString());
                }
            }
        });
    }

    public void navigateToHome() {
        Stage stage = (Stage) tbCustomers.getScene().getWindow();
        stage.close();
    }

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

    public void addCustomer() {
        navigateToEditor(null);
    }

    public void modifyCustomer() {
        Customer target = tbCustomers.getSelectionModel().getSelectedItem();
        if (target != null) {
            lbEvent.setText("");
            navigateToEditor(target);
        } else
            lbEvent.setText("Unknown target: Select the customer in the table and press modify again.");
    }

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
                        CustomerRepository.getInstance().deleteCustomer(target);
                        lbEvent.setText("Deletion successful: Customer has been removed from the database.");
                    }, () -> lbEvent.setText("Deletion aborted: No changes has been made to the database.")
            );
        } else {
            lbEvent.setText("Unknown target: Select the customer in the table and press delete again.");
        }
    }

}

package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.model.Country;
import edu.wgu.qam2schedulingapp.model.Customer;
import edu.wgu.qam2schedulingapp.model.EditorMode;
import edu.wgu.qam2schedulingapp.model.SPR;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.repository.LocationRepository;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller for the Customer Editor view in the scheduling app.
 * It handles the user interactions with the Customer Editor interface.
 * The editor supports both adding new customers and modifying existing ones.
 * It also validates user inputs before applying changes.
 *
 * @author Parham Modirniya
 */

public class CustomerEditorController implements Initializable {
    public TextField tfId;
    public TextField tfName;
    public TextField tfAddress;
    public TextField tfPostalCode;
    public TextField tfPhone;
    public ComboBox<Country> cbCountry;
    public ComboBox<SPR> cbSPR;
    public Label lbTitle;
    private EditorMode mode = EditorMode.Add;
    private static final String TAG = "CustomerEditorController";
    private final LocationRepository lr = LocationRepository.getInstance();

    /**
     * Initializes the CustomerEditorController.
     * This method is called after all FXMLLoader has completely loaded the FXML.
     * It initializes the log and populates the country combo box.
     *
     * @param url            A location used to resolve relative paths for the root object, or null.
     * @param resourceBundle The resources used to localize the root object, or null.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initLog(TAG);
        populateCbCountries();
    }

    /**
     * Updates the user interface according to the specified customer.
     * It sets the mode to modify if the specified customer is not null, and to add otherwise.
     * If the mode is set to modify, it also fills the fields with the customer's data.
     *
     * @param customer The customer to be edited, or null to add a new customer.
     */
    public void updateUI(Customer customer) {
        Logs.info(TAG, "Updating CustomerEditorController UI in " +
                       (customer != null ? "adding" : "modifying") + " mode");
        if (customer != null) {
            mode = EditorMode.Modify;
            SPR spr = lr.getSPRByDivisionId(customer.getDivisionId());
            Country country = lr.getCountryByDivisionId(customer.getDivisionId());
            cbCountry.setValue(country);
            cbSPR.getSelectionModel().select(spr);
            lbTitle.setText("Modify Customer");
            tfId.setText(String.valueOf(customer.getId()));
            tfName.setText(customer.getName());
            tfAddress.setText(customer.getAddress());
            tfPhone.setText(customer.getPhone());
            tfPostalCode.setText(customer.getPostalCode());
        }
    }

    /**
     * Applies the changes made in the editor.
     * It creates or modifies a customer according to the mode and the user inputs.
     * The changes are applied only if the user inputs are valid.
     */
    public void applyChange() {
        if (areEntriesValid()) {
            Customer customer = new Customer();
            if (mode == EditorMode.Modify) customer.setId(Integer.parseInt(tfId.getText()));
            customer.setName(tfName.getText());
            customer.setPhone(tfPhone.getText());
            customer.setAddress(tfAddress.getText());
            customer.setPostalCode(tfPostalCode.getText());
            customer.setDivisionId(cbSPR.getValue().getDivisionId());
            switch (mode) {
                case Add -> CustomerRepository.getInstance().addCustomer(customer);
                case Modify -> CustomerRepository.getInstance().updateCustomer(customer);
            }
            cancel();
        }
    }

    /**
     * Closes the Customer Editor view.
     */
    public void cancel() {
        Logs.info(TAG, "Closing the customer editor");
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }

    /**
     * Populates the country combo box.
     * It sets the items for the combo box and adds a listener to its selection.
     */
    private void populateCbCountries() {
        cbCountry.setItems(lr.allCountries);
        cbCountry.getSelectionModel().selectedItemProperty().addListener(
                (o, prevCountry, country) -> {
                    Logs.info(TAG, "cbCountries onChangeListener triggered");
                    if (country != null) {
                        cbSPR.setDisable(false);
                        cbSPR.setItems(lr.getAllSPRByCountryId(country.getId()));
                    }
                });
    }

    /**
     * Checks if the user inputs are valid.
     * It checks if none of the fields are empty and shows an error alert if any of them is.
     *
     * @return true if the user inputs are valid, false otherwise.
     */
    private boolean areEntriesValid() {
        Logs.info(TAG, "Validating customer entries");
        StringBuilder potentialErrors = new StringBuilder();
        if (tfName.getText().isBlank()) potentialErrors.append("\t‣ Name\n");
        if (tfPhone.getText().isBlank()) potentialErrors.append("\t‣ Phone\n");
        if (cbCountry.getSelectionModel().getSelectedItem() == null) potentialErrors.append("\t‣ Country\n");
        if (cbSPR.getSelectionModel().getSelectedItem() == null) potentialErrors.append("\t‣ State/Province/Region\n");
        if (tfAddress.getText().isBlank()) potentialErrors.append("\t‣ Address\n");
        if (tfPostalCode.getText().isBlank()) potentialErrors.append("\t‣ Postal Code\n");
        if (!potentialErrors.isEmpty()) {
            Logs.warning(TAG, "Validating customer entries has failed");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incomplete Entries");
            alert.setHeaderText("Fields bellow cannot be empty!");
            alert.setContentText(potentialErrors.toString());
            alert.show();
        }
        return potentialErrors.isEmpty();
    }
}
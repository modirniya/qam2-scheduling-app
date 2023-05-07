package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Country;
import edu.wgu.qam2schedulingapp.model.Customer;
import edu.wgu.qam2schedulingapp.model.EditorMode;
import edu.wgu.qam2schedulingapp.model.SPR;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.repository.LocationRepository;
import edu.wgu.qam2schedulingapp.utility.Logs;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerEditorController implements Initializable {


    private Customer customer;


    private static final String TAG = "CustomerEditorController";
    public TextField tfId;
    public TextField tfName;
    public TextField tfAddress;
    public TextField tfPostalCode;
    public ComboBox<Country> cbCountry;
    public ComboBox<SPR> cbSPR;
    public TextField tfPhone;
    public Label lbTitle;
    private EditorMode mode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initControllerLog(TAG);
        populateCbCountries();
    }

    private void populateCbCountries() {
        cbCountry.setItems(LocationRepository.allCountries);
        cbCountry.getSelectionModel().selectedItemProperty().addListener(
                (o, prevCountry, country) -> {
                    Logs.info(TAG, "cbCountries onChangeListener triggered");
                    if (country != null) {
                        cbSPR.setDisable(false);
                        cbSPR.setItems(LocationRepository.getAllSPRByCountryId(country.getId()));
                    }
                });
    }

    public void updateUI(Customer customer) {
        mode = customer == null ? EditorMode.Add : EditorMode.Modify;
        Logs.info(TAG, "Updating CustomerEditorController UI in " +
                       (mode == EditorMode.Add ? "adding" : "modifying") + " mode");
        this.customer = customer;
        switch (mode) {
            case Add -> lbTitle.setText("Add Customer");
            case Modify -> {
                SPR spr = LocationRepository.getSPRByDivisionId(customer.getDivisionId());
                Country country = LocationRepository.getCountryByDivisionId(customer.getDivisionId());
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
    }

    public void applyChange() {
        if (areEntriesValid()) {
            Customer customer = new Customer();
            customer.setId(this.customer.getId());
            customer.setName(tfName.getText());
            customer.setPhone(tfPhone.getText());
            customer.setAddress(tfAddress.getText());
            customer.setPostalCode(tfPostalCode.getText());
            customer.setDivisionId(cbSPR.getValue().getDivisionId());
            switch (mode) {
                case Add -> CustomerRepository.getInstance().addCustomer(customer);
                case Modify -> CustomerRepository.getInstance().updateCustomer(customer);
            }
            closeEditor();
        }
    }

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

    public void closeEditor() {
        Logs.info(TAG, "Closing the customer editor");
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}
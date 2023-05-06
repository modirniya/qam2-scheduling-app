package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Country;
import edu.wgu.qam2schedulingapp.model.Customer;
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

    private Customer currentCustomer;

    public enum EditorMode {
        Add,
        Modify
    }

    private static final String TAG = "CustomerEditorController";
    public TextField tfId;
    public TextField tfName;
    public TextField tfAddress;
    public TextField tfPostalCode;
    public ComboBox<Country> cbCountry;
    public ComboBox<SPR> cbSPR;
    public TextField tfPhone;
    public Label lbTitle;
    private EditorMode currentMode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initControllerLog(TAG);
        populateCbCountries();
    }

    private void populateCbCountries() {
        cbCountry.setItems(LocationRepository.allCountries);
        cbCountry.getSelectionModel().selectedItemProperty().addListener(
                (o, prevCountry, country) -> {
                    Logs.info(TAG, "cbCountries onChangeListener triggered: prev->" + prevCountry + "\tcountry->" + country);
                    if (country != null) {
                        cbSPR.setDisable(false);
                        cbSPR.setItems(LocationRepository.getAllSPRByCountryId(country.getId()));
                    }
                });
    }

    public void updateUI(EditorMode mode, Customer target) {
        Logs.info(TAG, "Updating CustomerEditorController UI");
        currentCustomer = target;
        currentMode = mode;
        switch (mode) {
            case Add -> lbTitle.setText("Add Customer");
            case Modify -> {
                SPR spr = LocationRepository.getSPRByDivisionId(target.getDivisionId());
                Country country = LocationRepository.getCountryByDivisionId(target.getDivisionId());
                cbCountry.setValue(country);
                cbSPR.getSelectionModel().select(spr);
                lbTitle.setText("Modify Customer");
                tfId.setText(String.valueOf(target.getId()));
                tfName.setText(target.getName());
                tfAddress.setText(target.getAddress());
                tfPhone.setText(target.getPhone());
                tfPostalCode.setText(target.getPostalCode());
            }
        }
    }

    public void applyChange() {
        if (areEntriesValid()) {
            var customer = new Customer(
                    currentMode == EditorMode.Add ? -1 : currentCustomer.getId(),
                    tfName.getText(),
                    tfPhone.getText(),
                    tfAddress.getText(),
                    tfPostalCode.getText(),
                    cbSPR.getValue().getDivisionId());
            switch (currentMode) {
                case Add -> CustomerRepository.getInstance().addCustomer(customer);
                case Modify -> CustomerRepository.getInstance().updateCustomer(customer);
            }
            cancel();
        }
    }

    private boolean areEntriesValid() {
        StringBuilder potentialErrors = new StringBuilder();
        if (tfName.getText().isBlank()) potentialErrors.append("\t‣ Name\n");
        if (tfPhone.getText().isBlank()) potentialErrors.append("\t‣ Phone\n");
        if (cbCountry.getSelectionModel().getSelectedItem() == null) potentialErrors.append("\t‣ Country\n");
        if (cbSPR.getSelectionModel().getSelectedItem() == null) potentialErrors.append("\t‣ State/Province/Region\n");
        if (tfAddress.getText().isBlank()) potentialErrors.append("\t‣ Address\n");
        if (tfPostalCode.getText().isBlank()) potentialErrors.append("\t‣ Postal Code\n");
        if (!potentialErrors.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incomplete Entries");
            alert.setHeaderText("Fields bellow cannot be empty!");
            alert.setContentText(potentialErrors.toString());
            alert.show();
        }
        return potentialErrors.isEmpty();
    }

    public void cancel() {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}
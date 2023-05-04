package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Country;
import edu.wgu.qam2schedulingapp.model.Customer;
import edu.wgu.qam2schedulingapp.model.SPR;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.repository.LocationRepository;
import edu.wgu.qam2schedulingapp.utility.Logs;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerEditorController implements Initializable {


    public enum EditorMode {
        Add,
        Modify
    }

    private static final String TAG = "CustomerEditorController";
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
    }

    private void populateCbCountries() {
        cbCountry.setItems(LocationRepository.allCountries);
        cbCountry.getSelectionModel().selectedItemProperty().addListener(
                (o, ov, country) -> {
                    cbSPR.setDisable(false);
                    if (country != null)
                        cbSPR.setItems(LocationRepository.getAllSPRByCountryId(country.getId()));
                });
    }

    public void updateUI(EditorMode mode, Customer target) {
        Logs.info(TAG, "Updating CustomerEditorController UI");
        currentMode = mode;
        clearAllFields();
        populateCbCountries();
        switch (mode) {
            case Add -> lbTitle.setText("Add Customer");
            case Modify -> {
                lbTitle.setText("Modify Customer");
                cbCountry.setValue(LocationRepository.getCountryByDivisionId(target.getDivisionId()));
                cbSPR.setValue(LocationRepository.getSPRByDivisionId(target.getDivisionId()));
                tfName.setText(target.getName());
                tfAddress.setText(target.getAddress());
                tfPhone.setText(target.getPhone());
                tfPostalCode.setText(target.getPostalCode());
            }
        }
    }

    private void clearAllFields() {
        tfName.setText("");
        tfAddress.setText("");
        tfPhone.setText("");
        tfPostalCode.setText("");
        cbCountry.setItems(null);
        cbSPR.setItems(null);
        cbSPR.setValue(null);
        cbSPR.setDisable(true);
    }

    public void applyChange() {
        switch (currentMode) {
            case Add -> {
                var customer = new Customer(tfName.getText(),
                        tfPhone.getText(),
                        tfAddress.getText(),
                        tfPostalCode.getText(),
                        cbSPR.getValue().getDivisionId());
                CustomerRepository.getInstance().addCustomer(customer);
            }
        }
    }

    public void cancel() {
        Stage stage = (Stage) cbCountry.getScene().getWindow();
        stage.close();
    }
}

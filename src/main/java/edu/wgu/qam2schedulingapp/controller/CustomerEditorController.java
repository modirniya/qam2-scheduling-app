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


    private Customer currentCustomer;

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
                        //cbSPR.
                    }
                });
    }

    public void updateUI(EditorMode mode, Customer target) {
        Logs.info(TAG, "Updating CustomerEditorController UI");
        currentCustomer = target;
        currentMode = mode;
        switch (mode) {
            case Add -> {
                lbTitle.setText("Add Customer");
//                clearAllFields();
            }
            case Modify -> {
                SPR spr = LocationRepository.getSPRByDivisionId(target.getDivisionId());
                Country country = LocationRepository.getCountryByDivisionId(target.getDivisionId());
                cbCountry.setValue(country);
                cbSPR.getSelectionModel().select(spr);
                lbTitle.setText("Modify Customer");
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
        cbCountry.setValue(null);
    }

    public void applyChange() {
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

    public void cancel() {
        clearAllFields();
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}

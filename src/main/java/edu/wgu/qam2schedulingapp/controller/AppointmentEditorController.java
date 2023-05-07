package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.*;
import edu.wgu.qam2schedulingapp.repository.ContactRepository;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.repository.UserRepository;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.TimeHelper;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentEditorController implements Initializable {

    private static final String TAG = "AppointmentEditorController";
    private EditorMode mode;
    private Appointment appointment;
    public Label lbTitle;
    public TextField tfAppointmentId;
    public TextField tfTitle;
    public TextField tfType;
    public TextField tfLocation;
    public TextField tfDescription;
    public ComboBox<Customer> cbCustomerId;
    public ComboBox<User> cbUserId;
    public ComboBox<Contact> cbContact;
    public ComboBox<String> cbStartTime;
    public ComboBox<String> cbEndTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initLog(TAG);
        cbStartTime.setItems(TimeHelper.getBusinessHours(0));
        cbEndTime.setItems(TimeHelper.getBusinessHours(+1));
        cbCustomerId.setItems(CustomerRepository.getInstance().allCustomers);
        cbUserId.setItems(UserRepository.getInstance().allUsers);
        cbContact.setItems(ContactRepository.getInstance().allContacts);
    }

    public void updateUI(Appointment appointment) {
        this.mode = appointment == null ? EditorMode.Add : EditorMode.Modify;
        this.appointment = appointment;
        Logs.info(TAG, "Updating AppointmentEditorController UI in " +
                       (mode == EditorMode.Add ? "adding" : "modifying") + " mode");
    }

    public void contactChosen(ActionEvent actionEvent) {
    }

    public void customerIdChosen(ActionEvent actionEvent) {
    }

    public void userIdChosen(ActionEvent actionEvent) {
    }

    public void startDateChosen(ActionEvent actionEvent) {
        var datePicker = (DatePicker) actionEvent.getTarget();
        cbStartTime.setDisable(datePicker.getValue() == null);
        Logs.info(TAG, "Start Date chosen: " + datePicker.getValue());
    }

    public void startTimeChosen(ActionEvent actionEvent) {
    }

    public void endDateChosen(ActionEvent actionEvent) {
        var datePicker = (DatePicker) actionEvent.getTarget();
        cbEndTime.setDisable(datePicker.getValue() == null);
        Logs.info(TAG, "End Date chosen: " + datePicker.getValue());
    }

    public void endTimeChosen(ActionEvent actionEvent) {
    }

    public void applyChanges(ActionEvent actionEvent) {
    }

    public void cancel(ActionEvent actionEvent) {
        Logs.info(TAG, "Closing the appointment editor");
        Stage stage = (Stage) tfAppointmentId.getScene().getWindow();
        stage.close();
    }
}

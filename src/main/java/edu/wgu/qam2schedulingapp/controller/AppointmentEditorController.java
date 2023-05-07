package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentEditorController implements Initializable {
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
    }

    public void updateUI(Appointment appointment) {
        this.mode = appointment == null ? EditorMode.Add : EditorMode.Modify;
        this.appointment = appointment;

    }

    public void contactChosen(ActionEvent actionEvent) {
    }

    public void customerIdChosen(ActionEvent actionEvent) {
    }

    public void userIdChosen(ActionEvent actionEvent) {
    }

    public void startDateChosen(ActionEvent actionEvent) {
    }

    public void startTimeChosen(ActionEvent actionEvent) {
    }

    public void endDateChosen(ActionEvent actionEvent) {

    }

    public void endTimeChosen(ActionEvent actionEvent) {
    }

    public void applyChanges(ActionEvent actionEvent) {
    }

    public void cancel(ActionEvent actionEvent) {
    }
}

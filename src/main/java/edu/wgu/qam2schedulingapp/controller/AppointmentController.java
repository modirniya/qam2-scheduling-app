package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.utility.Logs;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {
    private static final String TAG = "AppointmentController";
    public TableView<Appointment> tbAppointments;
    public Label lbEvent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initControllerLog(TAG);
    }

    public void navigateBackToHome(ActionEvent actionEvent) {
        Stage stage = (Stage) tbAppointments.getScene().getWindow();
        stage.close();
    }

    public void addAppointment(ActionEvent actionEvent) {
    }

    public void modifyAppointment(ActionEvent actionEvent) {
    }

    public void deleteAppointment(ActionEvent actionEvent) {
    }

    public void generateReport(ActionEvent actionEvent) {
    }
}

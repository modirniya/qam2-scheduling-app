package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.utility.LogsManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private static final String CUSTOMER_FXML = "/edu/wgu/qam2schedulingapp/view/customer.fxml";
    private static final String APPOINTMENT_FXML = "/edu/wgu/qam2schedulingapp/view/appointment.fxml";
    private static final String TAG = "HomeController";
    private final Stage customerStage = new Stage();
    private final Stage appointmentStage = new Stage();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LogsManager.infoLog(TAG, "Initiating...");
        FXMLLoader customerLoader = new FXMLLoader(getClass().getResource(CUSTOMER_FXML));
        FXMLLoader appointmentLoader = new FXMLLoader(getClass().getResource(APPOINTMENT_FXML));
        try {
            customerStage.initModality(Modality.APPLICATION_MODAL);
            appointmentStage.initModality(Modality.APPLICATION_MODAL);
            customerStage.setScene(new Scene(customerLoader.load()));
            appointmentStage.setScene(new Scene(appointmentLoader.load()));
        } catch (IOException e) {
            LogsManager.errorLog(TAG, "Initialization failed");
        }
    }

    public void showCustomersScreen() {
        customerStage.showAndWait();
    }

    public void showAppointmentScreen() {
        appointmentStage.showAndWait();
    }
}
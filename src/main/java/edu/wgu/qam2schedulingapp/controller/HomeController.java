package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.utility.Logs;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private static final String CUSTOMER_FXML = "/edu/wgu/qam2schedulingapp/view/customer.fxml";
    private static final String APPOINTMENT_FXML = "/edu/wgu/qam2schedulingapp/view/appointment.fxml";
    private static final String TAG = "HomeController";
    private final Stage stageCustomer = new Stage();
    private final Stage stageAppointment = new Stage();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initControllerLog(TAG);
        FXMLLoader loaderCustomer = new FXMLLoader(getClass().getResource(CUSTOMER_FXML));
        FXMLLoader loaderAppointment = new FXMLLoader(getClass().getResource(APPOINTMENT_FXML));
        try {
            stageCustomer.initModality(Modality.APPLICATION_MODAL);
            stageAppointment.initModality(Modality.APPLICATION_MODAL);
            stageCustomer.setScene(new Scene(loaderCustomer.load()));
            stageAppointment.setScene(new Scene(loaderAppointment.load()));
        } catch (IOException e) {
            Logs.error(TAG, "Initialization failed");
        }
    }

    public void showCustomersScreen() {
        stageCustomer.showAndWait();
    }

    public void showAppointmentScreen() {
        stageAppointment.showAndWait();
    }

    public void exit() {
        Platform.exit();
    }
}